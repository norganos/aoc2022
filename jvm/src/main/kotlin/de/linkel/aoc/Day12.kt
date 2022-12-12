package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import jakarta.inject.Singleton

@Singleton
class Day12: AbstractLinesAdventDay<Day12.Result>() {
    override val day = 12

    override fun process(lines: Sequence<String>): Result {
        var start = Point(0,0)
        var end = Point(0,0)
        val map = Grid.parse(lines) { p, c ->
            when (c) {
                'S' -> {
                    start = p
                    0
                }
                'E' -> {
                    end = p
                    25
                }
                else -> {
                    c - 'a'
                }
            }
        }
        val part1 = map.dijkstra(start, { it.point == end }) { p ->
            map.getNeighbours(p.point)
                .filter { it.data - p.data <= 1 }
                .map { it.point }
        }
        val part2 = map.dijkstra(end, { it.data == 0 }) { p ->
            map.getNeighbours(p.point)
                .filter { p.data - it.data <= 1 }
                .map { it.point }
        }

        return Result(
            part1?.let { it.size - 1 } ?: -1,
            part2?.let { it.size - 1 } ?: -1,
        )
    }

    data class Result(
        val steps: Int,
        val shortestDecline: Int
    ) {
        override fun toString(): String {
            return "steps from start to end: $steps, shortest path from end to a: $shortestDecline"
        }
    }
}
