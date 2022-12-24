package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Area
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import jakarta.inject.Singleton
import java.lang.IllegalStateException


@Singleton
class Day24: AbstractLinesAdventDay<Day24.Result>() {
    override val day = 24

    val still = Vector(0, 0)
    val north = Vector(0, -1)
    val east = Vector(1, 0)
    val south = Vector(0, 1)
    val west = Vector(-1, 0)

    override fun process(lines: Sequence<String>): Result {
        val map = Grid.parse(lines) { _, c ->
            Tile(
                wall = c == '#',
                blizzards = when(c) {
                    '^' -> listOf(north)
                    '>' -> listOf(east)
                    'v' -> listOf(south)
                    '<' -> listOf(west)
                    else -> emptyList()
                }
            )
        }
        val start = map.getRowData(map.area.origin.y).first { !it.data.wall }.point
        val dest = map.getRowData(map.area.origin.y + map.area.height - 1).first { !it.data.wall }.point
        val trip1 = bfs(start, dest, map)
        val trip2 = bfs(dest, start, trip1.first)
        val trip3 = bfs(start, dest, trip2.first)

        return Result(
            trip1.second.size - 1,
            trip1.second.size + trip2.second.size + trip3.second.size - 1,
        )
    }

    private fun bfs(start: Point, end: Point, initMap: Grid<Tile>): Pair<Grid<Tile>, List<Point>> {
        val queue = mutableListOf(listOf(start))
        val visitedStates = mutableSetOf<Point>()
        var round = 0
        var map = initMap
        while (true) {
            val path = queue.removeAt(0)
            if (path.last() == end) {
                return Pair(map, path)
            }
            if (path.size > 5 && path.takeLast(5).distinct().size == 1) {
                continue
            }
            if (path.size > round) {
                round++
                map = traverseMap(map)
                visitedStates.clear()
            }
            if (path.size < round) {
                throw IllegalStateException("map is already in round $round, can't process path for round ${path.size}")
            }
            val pos = path.last()
            if (pos in visitedStates) {
                continue
            }
            visitedStates.add(pos)
            queue.addAll(
                listOf(still, north, east, south, west)
                    .map { pos + it }
                    .filter { it in map && !map[it]!!.wall && map[it]!!.blizzards.isEmpty() }
                    .map { path + it }
            )
        }
    }

    private fun traverseMap(map: Grid<Tile>): Grid<Tile> {
        val mapArea = map.area
        val blizzardArea = Area(mapArea.x + 1, mapArea.y + 1, mapArea.width - 2, mapArea.height - 2)
        return map.transformComplete { points ->
            val next = mutableMapOf<Point, TmpTile>()
            next.putAll(
                points
                    .mapValues {
                        TmpTile(
                            wall = it.value.wall,
                            oldBlizzards = it.value.blizzards
                        )
                    }
            )
            next.entries.forEach { p ->
                p.value.oldBlizzards.forEach { v ->
                    val np = (p.key + v)
                        .let {
                            if (it.x < blizzardArea.x)
                                Point(blizzardArea.x + blizzardArea.width - 1, it.y)
                            else if (it.y < blizzardArea.y)
                                Point(it.x, blizzardArea.y + blizzardArea.height - 1)
                            else if (it.x >= blizzardArea.x + blizzardArea.width)
                                Point(blizzardArea.x, it.y)
                            else if (it.y >= blizzardArea.y + blizzardArea.height)
                                Point(it.x, blizzardArea.y)
                            else it
                        }
                    next[np]!!.newBlizzards.add(v)
                }
            }
            next.mapValues {
                Tile(
                    wall = it.value.wall,
                    blizzards = it.value.newBlizzards.toList()
                )
            }
        }
    }

    class Tile(
        val wall: Boolean = false,
        val blizzards: List<Vector>
    )
    class TmpTile(
        val wall: Boolean = false,
        val oldBlizzards: List<Vector>,
    ) {
        val newBlizzards = mutableListOf<Vector>()
    }

    data class Result(
        val part1: Int,
        val part2: Int
    ) {
        override fun toString(): String {
            return "$part1 steps forward, $part2 steps for forward, backward, forward again"
        }
    }
}
