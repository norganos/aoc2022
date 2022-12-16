package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import jakarta.inject.Singleton

@Singleton
class Day14(): AbstractLinesAdventDay<Day14.Result>() {
    override val day = 14

    val movements = listOf(
        Vector(0,1),
        Vector(-1, 1),
        Vector(1, 1)
    )

    override fun process(lines: Sequence<String>): Result {
        val map = Grid<Element>(Point(0, 0))
        lines.forEach { line ->
            val path = line.split(" -> ")
                .map {
                    val t = it.split(',')
                    Point(t[0].toInt(), t[1].toInt())
                }
            var last = path.first()
            map.stretchTo(last)
            map[last] = Element.ROCK
            path.drop(1).forEach { edge ->
                map.stretchTo(edge)
                (last upTo edge)
                    .forEach { p ->
                        map[p] = Element.ROCK
                    }
                last = edge
            }
        }

        var sandCounter1 = 0
        while(true) {
            val flow = sandflow(map)
            val rest = flow.firstOrNull { it !in map } ?: flow.last()
            if (rest in map) {
                map[rest] = Element.SAND
                sandCounter1++
            } else {
                break
            }
        }

        // we reuse the map from part 1 as sand would fill up in the same way first

        val box = map.getDataBoundingBox()
        val corridor = box.y + box.height + 1
        map.stretchTo(Point(box.x-box.height, corridor))
        map.stretchTo(Point(box.x+box.width+box.height, corridor))
        var sandCounter2 = sandCounter1
        while(true) {
            val flow = sandflow(map)
            val rest = flow.firstOrNull { it.y >= corridor } ?: flow.lastOrNull()
            if (rest == null) {
                break
            } else if (rest in map) {
                map[rest] = Element.SAND
            }
            sandCounter2++
        }

        return Result(
            sandCounter1,
            sandCounter2
        )
    }

    fun sandflow(map: Grid<Element>): Sequence<Point> {
        return sequence {
            var sand: Point? = Point(500,0)
            if (map[sand!!] == null) {
                while (sand != null) {
                    yield(sand)
                    sand = movements
                        .map { sand!! + it }
                        .firstOrNull { it !in map || map[it] == null }
                }
            }
        }
    }

    data class Result(
        val sandUnits1: Int,
        val sandUnits2: Int,
    ) {
        override fun toString(): String {
            return "units of sand before flowing: $sandUnits2"
        }
    }

    enum class Element {
        ROCK,
        SAND
    }
}
