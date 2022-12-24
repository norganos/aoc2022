package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Area
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton


@Singleton
class Day24(
    @Suppress("MnInjectionPoints") @Value("0") val rounds: Int = 0,
): AbstractLinesAdventDay<Day24.Result>() {
    override val day = 24

    val still = Vector(0, 0)
    val north = Vector(0, -1)
    val east = Vector(1, 0)
    val south = Vector(0, 1)
    val west = Vector(-1, 0)

    override fun process(lines: Sequence<String>): Result {
        val map = Grid.parse(lines) { pos, c ->
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
        val path = bfs(listOf(listOf(start)), dest, map)

        return Result(
            path.size - 1
        )
    }

    private fun bfs(paths: List<List<Point>>, end: Point, map: Grid<Tile>): List<Point> {
        val nextMap = traverseMap(map)
        val nextPathes = paths.flatMap { path ->
            val pos = path.last()
            listOf(still, north, east, south, west)
                .map { pos + it }
                .filter { it in nextMap && !nextMap[it]!!.wall && nextMap[it]!!.blizzards.isEmpty() }
                .map { path + it }
        }
        return nextPathes.firstOrNull { it.last() == end } ?: bfs(nextPathes, end, nextMap)
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
        val value: Int
    ) {
        override fun toString(): String {
            return "$value"
        }
    }
}
