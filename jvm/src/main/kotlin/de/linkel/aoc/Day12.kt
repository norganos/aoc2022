package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import jakarta.inject.Singleton
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import kotlin.math.abs
import kotlin.math.sign

@Singleton
class Day12(): AbstractLinesAdventDay<Day12.Result>() {
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
            if (c == 'S') {
                start = p
                0
            } else if (c == 'E') {
                end = p
                25
            } else {
                c - 'a'
            }
        }
//        val path = bfs(map, listOf(start), end)
//        val path = dfs(map, listOf(start), end)
//        val path = idfs(map, start, end)
        val part1 = dijkstra(map, start, { it == end}, { from, to -> map[to]!! - map[from]!! <= 1 })
        val part2 = dijkstra(map, end, { map[it] == 0 }, { from, to -> map[from]!! - map[to]!! <= 1})

        return Result(
            part1?.let { it.size - 1 } ?: -1,
            part2?.let { it.size - 1 } ?: -1,
        )
    }

    private fun bfs(map: Grid<Int>, path: List<Point>, dest: Point): List<Point> {
        if (path.isEmpty()) {
            throw IllegalArgumentException("empty initial path")
        }
        if (path.last() == dest) {
            return path
        }
        if (path.size >= map.width * map.height) {
            // if we visited every pos, we screwed up
            return emptyList()
        }
        return getPossibleBfsSteps(map, path)
            .map { next ->
                bfs(map, path + listOf(next), dest)
            }
            .filter { it.isNotEmpty() }
            .minByOrNull { it.size } ?: emptyList()
    }

    private fun getPossibleBfsSteps(map: Grid<Int>, path: List<Point>): Collection<Point> {
        val current = path.last()
        val currentHeight = map[current]!!
        return directions
            .map { current + it }
            .filter { it in map }
            .filter { it !in path }
            .filter { abs(map[it]!! - currentHeight) <= 1 }
    }

    private fun idfs(map: Grid<Int>, start: Point, dest: Point): List<Point>? {
        for (i in (dest-start).length until map.width * map.height) {
            val res = dfs(i, map, listOf(start), dest, null)
            if (res != null) {
                return res
            }
        }
        return null
    }

    private fun dfs(maxDepth: Int, map: Grid<Int>, path: List<Point>, dest: Point, blacklist: MutableSet<Point>? = mutableSetOf()): List<Point>? {
        if (path.isEmpty()) {
            throw IllegalArgumentException("empty initial path")
        }
        if (path.last() == dest) {
            return path
        }
        if (maxDepth > 0 && path.size >= maxDepth) {
            return null
        }
        val nexts = getPossibleDfsSteps(map, path, dest)
        if (nexts.isEmpty()) {
//            println("sackgasse")
            return null
        }
        if (blacklist != null && path.last() in blacklist) {
//            println("blacklisted")
            return null
        }

        val result = mutableListOf<List<Point>>()
        for (next in nexts) {
//            val nextPath = path + listOf(next)
            if (next == dest) {
                return path + listOf(next)
            }
//            nextPath.log()
            val solution = dfs(maxDepth, map, path + listOf(next), dest, blacklist)
            if (solution != null) {
                result.add(solution)
            }
        }
        if (result.isEmpty()) {
            blacklist?.add(path.last())
        }
        return result.minByOrNull { it.size }
    }

    private fun getPossibleDfsSteps(map: Grid<Int>, path: List<Point>, dest: Point): Collection<Point> {
        val current = path.last()
        val currentHeight = map[current]!!
        return getDirections(current, dest)
//        return directions
            .map { current + it }
            .filter { it in map }
            .filter { it !in path }
            .filter { map[it]!! - currentHeight <= 1 }
    }

    private fun getDirections(pos: Point, dest: Point): List<Vector> {
        val beeline = dest - pos
        return if (abs(beeline.deltaX) > abs(beeline.deltaY)) {
            val heading = beeline.deltaX.sign
                .let { if (it == 0) 1 else it }
            val orthogonal = beeline.deltaY.sign
                .let { if (it == 0) 1 else it }
            listOf(
                Vector(heading, 0),
                Vector(0, orthogonal),
                Vector(0, -orthogonal),
                Vector(-heading, 0)
            )
        } else {
            val heading = beeline.deltaY.sign
                .let { if (it == 0) 1 else it }
            val orthogonal = beeline.deltaX.sign
                .let { if (it == 0) 1 else it }
            listOf(
                Vector(0, heading),
                Vector(orthogonal, 0),
                Vector(-orthogonal, 0),
                Vector(0, -heading)
            )
        }
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
            var _prev: Point? = dest
            val result = mutableListOf<Point>()
            while (_prev != null) {
                result.add(0, _prev)
                _prev = weightMap[_prev]!!.before
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

    private fun List<Point>.log() {
        val sb = StringBuilder()
        val start = this.first()
        sb.append(start.toString())
        sb.append(" ")
        sb.append(this.drop(1).mapIndexed { i, p ->
            val vector = (p - this[i])
            if (vector.deltaX > 0 && vector.deltaY == 0) {
                "e"
            } else if (vector.deltaX < 0 && vector.deltaY == 0) {
                "w"
            } else if (vector.deltaX == 0 && vector.deltaY < 0) {
                "n"
            } else if (vector.deltaX == 0 && vector.deltaY > 0) {
                "s"
            } else {
                "?"
            }
        }.joinToString(""))
        sb.append(" ")
        sb.append(this.last().toString())
        println(sb.toString())
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
