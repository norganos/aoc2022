package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton


@Singleton
class Day04: AbstractLinesAdventDay<Day04.Result>() {
    companion object {
        private fun IntRange.contains(range: IntRange): Boolean {
            return this.contains(range.first) && this.contains(range.last)
        }
        private fun IntRange.overlaps(range: IntRange): Boolean {
            return this.contains(range.first) || this.contains(range.last)
        }
    }

    override val day = 4


    override fun process(lines: Sequence<String>): Result {
        val sums = lines
            .map { line ->
                val ranges = line.split(",")
                    .map { str ->
                        val dash = str.indexOf("-")
                        IntRange(str.substring(0, dash).toInt(), str.substring(dash + 1).toInt())
                    }
                assert(ranges.size == 2)
                Pair(ranges[0], ranges[1])
            }
            .fold(OverlapCounter()) { sums, pair ->
                sums + pair
            }
        return Result(sums.embedded, sums.overlapping)
    }

    data class OverlapCounter(
        val embedded: Int = 0,
        val overlapping: Int = 0
    ) {
        operator fun plus(rangePair: Pair<IntRange, IntRange>): OverlapCounter {
            return this.copy(
                embedded = this.embedded + if (rangePair.first.contains(rangePair.second) || rangePair.second.contains(rangePair.first)) 1 else 0,
                overlapping = this.overlapping + if (rangePair.first.overlaps(rangePair.second) || rangePair.second.overlaps(rangePair.first)) 1 else 0
            )
        }
    }

    data class Result(
        val contained: Int,
        val overlapping: Int
    ) {
        override fun toString(): String {
            return "completely contained: $contained, overlapping: $overlapping"
        }
    }
}
