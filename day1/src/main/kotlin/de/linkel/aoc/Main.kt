package de.linkel.aoc

import java.io.File

fun main(args: Array<String>) {
    val topCount = 3
    File(args.firstOrNull() ?: "input.txt").bufferedReader().use { reader ->
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
