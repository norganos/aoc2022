package de.linkel.aoc

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton
import kotlin.math.min

@Singleton
class Day13(
    val objectMapper: ObjectMapper
): AbstractLinesAdventDay<Day13.Result>() {
    override val day = 13

    override fun process(lines: Sequence<String>): Result {
        val jsonNodes = lines
            .filter { it.isNotBlank() }
            .map { Pair(it, objectMapper.readTree(it)) }
            .toList()
        val pairs = jsonNodes
            .chunked(2)
            .map { Pair(it[0].second, it[1].second) }
        val correctIndices = pairs.mapIndexed { i, p ->
//            println("== Pair ${i+1} ==")
            if (compare(p.first, p.second) == -1) i+1 else 0
        }
        val ordered = (jsonNodes + listOf("[[2]]","[[6]]").map { Pair(it, objectMapper.readTree(it)) })
            .sortedWith { a, b ->
                compare(a.second, b.second)
            }
        val divider2 = ordered.indexOfFirst { it.first == "[[2]]" } + 1
        val divider6 = ordered.indexOfFirst { it.first == "[[6]]" } + 1


        return Result(
            sum = correctIndices.sum(),
            key = divider2 * divider6
        )
    }
    fun compare(a: JsonNode, b: JsonNode, indent: Int = 0): Int {
//        println("${" ".repeat(indent)}- Compare ${a} vs ${b}")
        if (a.isNumber && b.isNumber) {
            return if (a.intValue() < b.intValue()) {
//                println("${" ".repeat(indent+2)}- Left side is smaller, so inputs are in the right order")
                -1
            } else if (a.intValue() > b.intValue()) {
//                println("${" ".repeat(indent+2)}- Right side is smaller, so inputs are not in the right order")
                1
            } else {
                0
            }
        } else if (a.isArray && b.isArray) {
            for (i in 0 until min(a.size(), b.size())) {
                val tmp = compare(a[i], b[i], indent+2)
                if (tmp != 0) {
                    return tmp
                }
            }
            return if (a.size() < b.size()) {
//                println("${" ".repeat(indent+2)}- Left side ran out of items, so inputs are in the right order")
                -1
            } else if (a.size() > b.size()) {
//                println("${" ".repeat(indent+2)}- Right side ran out of items, so inputs are not in the right order")
                1
            } else {
                0
            }
        } else if (a.isNumber && b.isArray) {
//            println("${" ".repeat(indent)}  - Mixed types; convert left to [${a}] and retry comparison")
            return compare(objectMapper.nodeFactory.arrayNode(1).add(a.intValue()), b, indent+2)
        } else if (a.isArray && b.isNumber) {
//            println("${" ".repeat(indent)}  - Mixed types; convert right to [${b}] and retry comparison")
            return compare(a, objectMapper.nodeFactory.arrayNode(1).add(b.intValue()), indent+2)
        } else {
            throw IllegalArgumentException("don't know how to compare $a and $b")
        }
    }

    data class Result(
        val sum: Int,
        val key: Int,
    ) {
        override fun toString(): String {
            return "steps of indices in right order: $sum, decoder key is $key"
        }
    }
}
