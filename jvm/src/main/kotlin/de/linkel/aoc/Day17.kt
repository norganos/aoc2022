package de.linkel.aoc

import de.linkel.aoc.base.AbstractFileAdventDay
import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import de.linkel.aoc.utils.readers.ReaderSequence
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.io.BufferedReader
import java.io.Reader
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.util.*
import java.util.regex.Pattern
import kotlin.math.max

@Singleton
class Day17(
    @Suppress("MnInjectionPoints") @Value("1000000000000") val blockCount: Long = 1000000000000L
): AbstractFileAdventDay<Day17.Result>() {
    override val day = 17

    val left = Vector(-1, 0)
    val right = Vector(1, 0)
    val down = Vector(0, -1)

    override fun process(reader: BufferedReader): Result {
        val jetPattern = ReaderSequence(reader)
            .filter { !it.isWhitespace()}
            .map { c ->
                when (c) {
                    '<' -> left
                    '>' -> right
                    else -> throw IllegalArgumentException("unknown char '$c'")
                }
            }
            .toList()


        val chamber = Chamber()
        val jets = jetSequence(jetPattern).iterator()
        var steps = 0L
        rockSequence()
//            .take(11)
            .takeWhile { steps++ < blockCount }
            .forEach { rockTemplate ->
                var rock = rockTemplate.plus(Vector(0, chamber.minY + 4))
//                println("\n\n\n")
//                chamber.print(rock)
                while (true) {
//                    steps++
                    var r = rock + jets.next()
                    rock = if (r in chamber && !chamber.blocks(r)) r else rock
                    r = rock + down
                    if (chamber.blocks(r)) {
                        chamber.put(rock)
                        break
                    } else {
                        rock = r
                    }
                }
            }
//        chamber.print(null)
//        println("used $steps steps")

        return Result(
            chamber.minY
        )
    }

    class Chamber(
        val minX: Int = 0,
        val maxX: Int = 6
    ) {
        var minY = 0L

        var topRocks: List<Shape> = emptyList()

        operator fun contains(s: Shape): Boolean {
            return s.points.all { it.x in minX..maxX }
        }

        fun blocks(s: Shape): Boolean {
            return topRocks.any { it.intersects(s) } || s.points.any { it.y == 0 }
        }

        fun put(s: Shape) {
            val xx = mutableSetOf(0,1,2,3,4,5,6)
            val backup = topRocks
            topRocks = (topRocks + s)
                .sortedByDescending { r -> r.points.maxOf { it.y } }
                .take(50)
//                .takeWhile { r ->
//                    val removed = xx.removeAll(r.points.map { it.x }.toSet())
//                    removed || xx.isNotEmpty()
//                }
//                .toList()
            minY = topRocks.maxOf { r -> r.points.maxOf { it.y } }
        }

        fun print(floating: Shape?) {

            (max(minY, floating?.points?.maxOf { it.y } ?: 0) downTo 1).forEach { y ->
                val rowRocks = topRocks.filter { r -> r.points.any { it.y == y }}.toList()
                print("|")
                (0 until 7).forEach { x ->
                    print(if (floating?.points?.any { it.x == x && it.y == y } == true) "@" else if (rowRocks.any { r -> r.points.any { it.y == y && it.x == x }}) "#" else ".")
                }
                println("|")
            }
            println("+-------+")
        }
    }

    val shapes = listOf(
        Shape(
            listOf(
                Point(2, 0),
                Point(3, 0),
                Point(4, 0),
                Point(5, 0)
            )
        ),
        Shape(
            listOf(
                Point(3, 2),
                Point(2, 1),
                Point(3, 1),
                Point(4, 1),
                Point(3, 0)
            )
        ),
        Shape(
            listOf(
                Point(4, 2),
                Point(4, 1),
                Point(2, 0),
                Point(3, 0),
                Point(4, 0)
            )
        ),
        Shape(
            listOf(
                Point(2, 3),
                Point(2, 2),
                Point(2, 1),
                Point(2, 0)
            )
        ),
        Shape(
            listOf(
                Point(2, 1),
                Point(3, 1),
                Point(2, 0),
                Point(3, 0)
            )
        )
    )

    fun rockSequence(): Sequence<Shape> {
        var pos = 0
        return generateSequence {
            shapes[pos++ % shapes.size]
        }
    }

    fun jetSequence(pattern: List<Vector>): Sequence<Vector> {
        var pos = 0
        return generateSequence {
            pattern[pos++ % pattern.size]
        }
    }

    data class Shape(
        val points: List<Point>
    ) {
        operator fun plus(v: Vector): Shape {
            return copy(
                points = points.map { it + v }
            )
        }
        fun intersects(s: Shape): Boolean {
            return points.any { it in s.points}
        }
    }

    data class Result(
        val height: Long
    ) {
        override fun toString(): String {
            return "rock is $height units tall"
        }
    }
}
