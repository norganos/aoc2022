package de.linkel.aoc

import de.linkel.aoc.utils.Input
import de.linkel.aoc.utils.TopList
import jakarta.inject.Singleton

@Singleton
class Day01: AdventDay {
    override val day = 1

    override fun solve(args: List<String>) {
        val topCount = 3
        Input.from(args, "input01.txt").use { reader ->
            reader.useLines { sequence ->
                val top = sequence
                    .fold(Pair(0, TopList<Int>(topCount))) { state, line ->
                        if (line.isEmpty()) {
                            state.copy(
                                first = 0,
                                second = state.second + state.first
                            )
                        } else {
                            state.copy(
                                first = state.first + line.toInt()
                            )
                        }
                    }
                    .let { state ->
                        state.second + state.first
                    }
                println("max: ${top.first()}")
                println("top3: ${top.sum()}")
            }
        }
    }
}
