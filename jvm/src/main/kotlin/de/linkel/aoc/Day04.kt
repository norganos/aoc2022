package de.linkel.aoc

import de.linkel.aoc.utils.Input
import jakarta.inject.Singleton


@Singleton
class Day04: AdventDay {
    companion object {
        private fun IntRange.contains(range: IntRange): Boolean {
            return this.contains(range.first) && this.contains(range.last)
        }
        private fun IntRange.overlaps(range: IntRange): Boolean {
            return this.contains(range.first) || this.contains(range.last)
        }
    }

    override val day = 4

    override fun solve(args: List<String>) {
        Input.from(args, "input04.txt").use { reader ->
            reader.useLines { sequence ->
                val sums = sequence
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
                println("completely contained: ${sums.embedded}")
                println("overlapping:          ${sums.overlapping}")
            }
        }
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
}
