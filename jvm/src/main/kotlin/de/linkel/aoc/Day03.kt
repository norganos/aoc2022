package de.linkel.aoc

import de.linkel.aoc.utils.Input
import jakarta.inject.Singleton

@Singleton
class Day03: AdventDay {
    override val day = 3

    fun prio(c: Char) = if (c.isLowerCase()) c - 'a' + 1 else c - 'A' + 27

    override fun solve(args: List<String>) {
        Input.from(args, "input03.txt").use { reader ->
            reader.useLines { sequence ->
                val sums = sequence
                    .chunked(3)
                    .map { group ->
                        val badges = group.first().toCharArray().filter { c ->
                            group.takeLast(2).all { it.contains(c) }
                        }.distinct()
                        assert(badges.size == 1)
                        val doubles = group.map { line ->
                            val middle = line.length / 2
                            val double = line.substring(0, middle).toCharArray()
                                .filter { line.indexOf(it, middle) > -1 }
                                .distinct()
                            assert(double.size == 1)
                            double.first()
                        }
                        Pair(badges.first(), doubles)
                    }
                    .fold(Pair(0, 0)) { sumPair, groupValues ->
                        sumPair.copy(
                            first = sumPair.first + prio(groupValues.first),
                            second = sumPair.second + groupValues.second.sumOf { prio(it) },
                        )
                    }
                println("double prio sum: ${sums.second}")
                println("badge prio sum: ${sums.first}")
            }
        }
    }
}
