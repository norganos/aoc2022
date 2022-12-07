package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.TopList
import jakarta.inject.Singleton

@Singleton
class Day01: AbstractLinesAdventDay<Day01.Result>() {
    override val day = 1

    val topCount = 3

    override fun process(lines: Sequence<String>): Result {
        val top = lines
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
        return Result(top.first(), top.sum())
    }

    data class Result(
        val max: Int,
        val top3: Int
    ) {
        override fun toString(): String {
            return "max: $max, top3: $top3"
        }
    }
}
