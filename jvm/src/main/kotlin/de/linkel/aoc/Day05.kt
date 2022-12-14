package de.linkel.aoc

import de.linkel.aoc.base.AbstractFileAdventDay
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.io.BufferedReader

@Singleton
class Day05(
    @Suppress("MnInjectionPoints") @Value("CrateMover9001") val crane: Crane = Crane.CrateMover9001
): AbstractFileAdventDay<Day05.Result>() {
    override val day = 5

    fun loadStacks(reader: BufferedReader): Map<String, Stack> {
        val stacks = mutableListOf<TmpStack>()
        while (true) {
            val line = reader.readLine()
            if (line.trim().isEmpty())
                break
            val count = (line.length + 1) / 4
            while (stacks.size < count) {
                stacks.add(TmpStack(crane))
            }
            stacks.forEachIndexed { i, p ->
                val token = line.substring(4 * i, 4 * i + 2)
                if (token != "  ") {
                    if (token[0] == '[') {
                        p.stack.deserialize(token[1])
                    } else if (token[0] == ' ') {
                        p.name = "${token[1]}"
                    }
                }
            }
        }
        return stacks.associate { it.name to it.stack }
    }

    override fun process(reader: BufferedReader): Result {
        val stacks = loadStacks(reader)
        reader.useLines { sequence ->
            sequence.forEach { line ->
                val tokens = line.trim().split(" ")
                assert(tokens.size == 6)
                assert(tokens[0] == "move")
                assert(tokens[2] == "from")
                assert(tokens[4] == "to")
                stacks[tokens[3]]!!.move(to = stacks[tokens[5]]!!, count = tokens[1].toInt())
            }
            val tops = stacks.keys.sorted().map { stacks[it]!!.top  }.joinToString("")
            return Result(tops)
        }
    }

    enum class Crane {
        CrateMover9000,
        CrateMover9001
    }

    class Stack(
        private val crane: Crane
    ) {
        private val content = mutableListOf<Char>()

        val top get(): Char = content.first()
        val stack get(): String = content.joinToString("")

        fun deserialize(c: Char) {
            content.add(c)
        }

        fun move(to: Stack, count: Int = 1) {
            when (crane) {
                Crane.CrateMover9000 -> {
                    repeat(count) {
                        val box = content.removeAt(0)
                        to.put(box)
                    }
                }
                Crane.CrateMover9001 -> {
                    val tmp = mutableListOf<Char>()
                    repeat(count) {
                        tmp.add(content.removeAt(0))
                    }
                    to.put(tmp)
                }
            }
        }

        private fun put(box: Char) {
            content.add(0, box)
        }
        private fun put(boxes: List<Char>) {
            boxes.reversed().forEach { box ->
                content.add(0, box)
            }
        }
    }

    class TmpStack(
        crane: Crane,
        var name: String = "?",
        val stack: Stack = Stack(crane)
    )

    data class Result(
        val tops: String
    ) {
        override fun toString(): String {
            return "tops: $tops"
        }
    }
}
