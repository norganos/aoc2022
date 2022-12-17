package de.linkel.aoc

import de.linkel.aoc.base.AbstractFileAdventDay
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import de.linkel.aoc.utils.readers.ReaderSequence
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.io.BufferedReader
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.max

@Singleton
class Day17(
    @Suppress("MnInjectionPoints") @Value("1000000000000") val blockCount: Long = 1000_000_000_000L
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
        val rocksStart = rockCycle(jetPattern, chamber, blockCount)
        val heightStart = chamber.savedHeight + chamber.minY
        val topShapeStart = chamber.topRocks.flatMap { it.points }
            .let { s ->
                val toBottom = Vector(0, -s.minOf { it.y })
                s.map { it + toBottom }
            }
            .toSet()
        val rocksPerCycle = rockCycle(jetPattern, chamber, blockCount - rocksStart)
        if (rocksPerCycle == 0L) {
            return Result(
                heightStart
            )
        }
        val heightPerCycle = chamber.savedHeight + chamber.minY - heightStart
        val topShapeAfterCycle = chamber.topRocks.flatMap { it.points }
            .let { s ->
                val toBottom = Vector(0, -s.minOf { it.y })
                s.map { it + toBottom }
            }
            .toSet()
        assert(topShapeStart == topShapeAfterCycle)
        val cycles = (blockCount - rocksStart) / rocksPerCycle
        val rest = blockCount - rocksStart - cycles * rocksPerCycle
        val rocksEnd = rockCycle(jetPattern, chamber, rest)
        assert(rocksStart + cycles * rocksPerCycle + rocksEnd == blockCount)
        val heightEnd = chamber.savedHeight + chamber.minY - heightPerCycle - heightStart
        return Result(
            heightStart + cycles * heightPerCycle + heightEnd
        )
//        if (blockCount > 2 * jetPattern.size) {
//            rockSequence()
//                .takeWhile { steps++ < jetPattern.size }
//                .forEach { rockRound(jets, chamber, it) }
//            steps = 0L
//            rockSequence()
//                .takeWhile { steps++ < jetPattern.size }
//                .forEach { rockRound(jets, chamber, it) }
//            val heightCycle2 = chamber.savedHeight + chamber.minY - heightCycle1
//            val rocksCycle2 = chamber.blockCount - rocksCycle1
//            val topShapeCycle2 = chamber.topRocks.flatMap { it.points }
//                .let { s ->
//                    val toBottom = Vector(0, -s.minOf { it.y })
//                    s.map { it + toBottom }
//                }
//                .toSet()
//            assert(topShapeCycle1 == topShapeCycle2)
//            steps = 0L
//            rockSequence()
//                .takeWhile { steps++ < (blockCount % jetPattern.size) }
//                .forEach { rockRound(jets, chamber, it) }
//            val heightTail = chamber.savedHeight + chamber.minY - heightCycle2 - heightCycle1
//            val rocksTail = chamber.blockCount - rocksCycle2 - rocksCycle1
//
//
//            return Result(
//                heightCycle1 + (blockCount / rocksCycle2 - 1) * heightCycle2 + heightTail
//            )
//        } else {
//            val chamber = Chamber()
//            var steps = 0L
//            val jets = jetSequence(jetPattern).iterator()
//            rockSequence()
//                .takeWhile { steps++ < blockCount }
//                .forEach { rockRound(jets, chamber, it) }
//            return Result(
//                chamber.savedHeight + chamber.minY
//            )
//        }
    }

    fun rockRound(jets: Iterator<Vector>, chamber: Chamber, rockTemplate: Shape) {
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

    fun rockCycle(jetPatterns: List<Vector>, chamber: Chamber, maxRocks: Long): Long {
        var rocks = 0L
        var j = 0
        var r = 0
        while (rocks < maxRocks) {
            if (rocks > 0 && j == 0 && r == 0) {
                return rocks
            }
            var rock = rockPatterns[r].plus(Vector(0, chamber.minY + 4))
            r = (r + 1) % rockPatterns.size
            while (true) {
                var r = rock + jetPatterns[j]
                j = (j + 1) % jetPatterns.size
                rock = if (r in chamber && !chamber.blocks(r)) r else rock
                r = rock + down
                if (chamber.blocks(r)) {
                    chamber.put(rock)
                    break
                } else {
                    rock = r
                }
            }
            rocks++
        }
        return rocks
    }

    class Chamber(
        val minX: Int = 0,
        val maxX: Int = 6
    ) {
        var minY = 0
        var savedHeight = 0L
        var blockCount = 0L

        var topRocks: List<Shape> = emptyList()

        operator fun contains(s: Shape): Boolean {
            return s.points.all { it.x in minX..maxX }
        }

        fun blocks(s: Shape): Boolean {
            return topRocks.any { it.intersects(s) } || s.points.any { it.y == 0 }
        }

        fun put(s: Shape) {
            blockCount++
//            val xx = mutableSetOf(0,1,2,3,4,5,6)
//            val backup = topRocks
            topRocks = (topRocks + s)
                .sortedByDescending { r -> r.points.maxOf { it.y } }
                .take(50)
//                .takeWhile { r ->
//                    val removed = xx.removeAll(r.points.map { it.x }.toSet())
//                    removed || xx.isNotEmpty()
//                }
//                .toList()
            minY = topRocks.maxOf { r -> r.points.maxOf { it.y } }
            if (minY > 10_000) {
                savedHeight += 10_000
                val transform = Vector(0,-10_000)
                topRocks = topRocks.map {
                    it + transform
                }
            }
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

    val rockPatterns = listOf(
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
            rockPatterns[pos++ % rockPatterns.size]
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
