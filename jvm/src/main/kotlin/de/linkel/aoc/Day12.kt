package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import jakarta.inject.Singleton

@Singleton
class Day12: AbstractLinesAdventDay<Day12.Result>() {
    override val day = 12

    private val directions = listOf(
        Vector(1, 0),
        Vector(0, 1),
        Vector(-1, 0),
        Vector(0, -1)
    )

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
        val part1 = dijkstra(map, start, { it == end}, { from, to -> map[to]!! - map[from]!! <= 1 })
        val part2 = dijkstra(map, end, { map[it] == 0 }, { from, to -> map[from]!! - map[to]!! <= 1})

        return Result(
            part1?.let { it.size - 1 } ?: -1,
            part2?.let { it.size - 1 } ?: -1,
        )
    }

    private fun dijkstra(map: Grid<Int>, start: Point, isDest: (point: Point) -> Boolean, canPass: (from: Point, to: Point) -> Boolean): List<Point>? {
        val max = map.maxSize + 1
        val weightMap = map.transform { p, h -> DijkstraNode(h, if (p == start) 0 else max, null) }
        val points = weightMap.getAllData().map { it.point }.toMutableSet()
        var dest: Point? = null
        while (points.isNotEmpty()) {
            val point = points.minBy { weightMap[it]!!.distance }
            points.remove(point)
            directions
                .map { point + it }
                .filter { it in map }
                .filter { it in points }
                .filter { canPass(point, it) }
                .forEach {
                    weightMap[it] = weightMap[it]!!.copy(distance = weightMap[point]!!.distance + 1, before = point)
                }
            if (isDest(point)) {
                dest = point
                break
            }
        }
        return if (dest != null) {
            var prev: Point? = dest
            val result = mutableListOf<Point>()
            while (prev != null) {
                result.add(0, prev)
                prev = weightMap[prev]!!.before
            }
            result
        } else {
            null
        }
    }

    data class DijkstraNode(
        val height: Int,
        val distance: Int,
        val before: Point?
    )

    data class Result(
        val steps: Int,
        val shortestDecline: Int
    ) {
        override fun toString(): String {
            return "steps from start to end: $steps, shortest path from end to a: $shortestDecline"
        }
    }
}
