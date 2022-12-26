package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton
import java.lang.IllegalStateException
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.pow


@Singleton
class Day25: AbstractLinesAdventDay<Day25.Result>() {
    override val day = 25

    override fun process(lines: Sequence<String>): Result {
        val sum = lines
            .map { snafu2int(it) }
            .sum()
        return Result(
            sum,
            int2snafu(sum)
        )
    }

    fun snafu2int(input: String): Long {
        return input.toCharArray()
            .map {
                when(it) {
                    '2' -> 2
                    '1' -> 1
                    '0' -> 0
                    '-' -> -1
                    '=' -> -2
                    else -> throw IllegalArgumentException("unknown character '$it'")
                }
            }
            .reversed()
            .foldIndexed(0L) { i, r, c ->
                r + 5.0.pow(i).toLong() * c
            }
    }

    fun int2snafu(input: Long): String {
        var tmp = input
        return ((ceil(ln(input.toDouble()) / ln(5.0)).toInt() + 1) downTo 0)
            .map { i ->
                var c = 0
                val t = 5.0.pow(i).toLong()
                while (tmp >= t) {
                    c++
                    tmp -= t
                }
                c
            }
            .reversed()
            .toMutableList()
            .onEach {
                if (it !in 0L..4L) {
                    throw IllegalStateException("wrong digit $it")
                }
                assert(it in 0L..4L)
            }
            .let { l ->
                l.forEachIndexed { i, c ->
                    when (c) {
                        4 -> {
                            l[i] = -1
                            l[i+1]++
                        }
                        3 -> {
                            l[i] = -2
                            l[i+1]++
                        }
                        5 -> {
                            l[i] = 0
                            l[i+1]++
                        }
                    }
                }
                l
            }
            .reversed()
            .map { i ->
                when(i) {
                    -2 -> '='
                    -1 -> '-'
                    0 -> '0'
                    1 -> '1'
                    2 -> '2'
                    else -> throw IllegalStateException("got digit $i")
                }
            }
            .joinToString("")
            .trimStart('0')
            .ifEmpty { "0" }
    }

    data class Result(
        val sum: Long,
        val sumSnafu: String
    ) {
        override fun toString(): String {
            return "sum: $sumSnafu [$sum]"
        }
    }
}
