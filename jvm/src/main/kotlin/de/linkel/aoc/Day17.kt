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

        val c = Chamber()
        val testdata = cycleDetector(jetPattern, c, 15_000)
        val testdatasize = testdata.size

        var cycleInfo: CycleInfo? = null
        for (cyclelen in 3 until 50) {
            val dropped = testdatasize % cyclelen
            val chunked = testdata.drop(dropped).chunked(cyclelen)
            val possibleCycle = chunked.last().map { CycleDataPattern(it.rocksDelta, it.heightDelta, it.rockIndex, it.jetIndex) }
            val iterations = chunked
                .reversed()
                .takeWhile { chunk -> chunk.map { CycleDataPattern(it.rocksDelta, it.heightDelta, it.rockIndex, it.jetIndex) } == possibleCycle }
                .count()
            if (iterations > 1) {
                println("found a cycle $possibleCycle")
                val chunkedCycleStart = chunked.size - iterations
                assert(chunked[chunkedCycleStart].map { CycleDataPattern(it.rocksDelta, it.heightDelta, it.rockIndex, it.jetIndex) } == possibleCycle)
                val cycleStart = chunkedCycleStart * cyclelen + dropped
                cycleInfo = CycleInfo(
                    beforeCycle = RockPile(testdata[cycleStart+cyclelen-1].rocks, testdata[cycleStart+cyclelen-1].height, testdata[cycleStart+cyclelen-1].jetIndex),
                    cycle = RockPile(possibleCycle.sumOf { it.rocksDelta }, possibleCycle.sumOf { it.heightDelta }, possibleCycle.last().jetIndex),
                )


                val simulated = Chamber()
                val calculated = Chamber()
                val proofCount = cycleInfo.beforeCycle.rocks + 123
                val simulatedPile = rockCycle(jetPattern, simulated, proofCount)
                val head = rockCycle(jetPattern, calculated, cycleInfo.beforeCycle.rocks)
                val cycleCount = (proofCount - cycleInfo.beforeCycle.rocks) / cycleInfo.cycle.rocks
                val remainder = (proofCount - cycleInfo.beforeCycle.rocks) % cycleInfo.cycle.rocks
                val tail = rockCycle(jetPattern, calculated, remainder, cycleInfo.beforeCycle.jetIndex)

                assert(cycleCount * cycleInfo.cycle.height + calculated.height == simulated.height)
                break
            }
        }



        val chamber = Chamber()
        return if (cycleInfo != null && blockCount > cycleInfo.beforeCycle.rocks + cycleInfo.cycle.rocks) {
            val head = rockCycle(jetPattern, chamber, cycleInfo.beforeCycle.rocks)
            val cycleCount = (blockCount - cycleInfo.beforeCycle.rocks) / cycleInfo.cycle.rocks
            val remainder = (blockCount - cycleInfo.beforeCycle.rocks) % cycleInfo.cycle.rocks
            val tail = rockCycle(jetPattern, chamber, remainder, cycleInfo.beforeCycle.jetIndex)

            Result(cycleCount * cycleInfo.cycle.height + chamber.height)
        } else {
            if (cycleInfo == null) {
                println("did not find a cycle :-/ have to simulate all $blockCount rocks")
            }
            rockCycle(jetPattern, chamber, blockCount)
            Result(chamber.height)
        }


//        val rocksStartState = rockCycle(jetPattern, chamber, blockCount)
//        val heightStart = chamber.savedHeight + chamber.minY
//        val topShapeStart = chamber.topRocks.flatMap { it.points }
//            .let { s ->
//                val toBottom = Vector(0, -s.minOf { it.y })
//                s.map { it + toBottom }
//            }
//            .toSet()
//        val rocksPerCycleState = rockCycle(jetPattern, chamber, blockCount - rocksStartState.rocks, rocksStartState.floating)
//        if (rocksPerCycleState.rocks == 0L) {
//            return Result(
//                heightStart
//            )
//        }
//        val heightPerCycle = chamber.savedHeight + chamber.minY - heightStart
//        val topShapeAfterCycle = chamber.topRocks.flatMap { it.points }
//            .let { s ->
//                val toBottom = Vector(0, -s.minOf { it.y })
//                s.map { it + toBottom }
//            }
//            .toSet()
////        assert(topShapeStart == topShapeAfterCycle)
//        val cycles = (blockCount - rocksStartState.rocks) / rocksPerCycleState.rocks
//        val rest = blockCount - rocksStartState.rocks - cycles * rocksPerCycleState.rocks
//        val rocksEndState = rockCycle(jetPattern, chamber, rest, rocksPerCycleState.floating)
////        assert(rocksStartState.rocks + cycles * rocksPerCycleState.rocks + rocksEndState.rocks == blockCount)
//        val heightEnd = chamber.savedHeight + chamber.minY - heightPerCycle - heightStart
//        return Result(
//            heightStart + cycles * heightPerCycle + heightEnd
//        )
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


    fun rockCycle(jetPatterns: List<Vector>, chamber: Chamber, maxRocks: Long, jetStart: Int = 0): RockPile {
        var rocks = 0L
        var j = jetStart
        var r = 0
        val startHeight = chamber.height
        while (rocks < maxRocks) {
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
        return RockPile(rocks, chamber.height - startHeight, j)
    }

    data class CycleData(
        val rocks: Long,
        val rocksDelta: Long,
        val height: Long,
        val heightDelta: Long,
        val rockIndex: Int,
        val jetIndex: Int
    )
    data class CycleDataPattern(
        val rocksDelta: Long,
        val heightDelta: Long,
        val rockIndex: Int,
        val jetIndex: Int
    )

    data class RockPile(
        val rocks: Long,
        val height: Long,
        val jetIndex: Int
    )
    data class CycleInfo(
        val beforeCycle: RockPile,
        val cycle: RockPile
    )
    fun cycleDetector(jetPatterns: List<Vector>, chamber: Chamber, maxRocks: Long): List<CycleData> {
        val result = mutableListOf<CycleData>()
        var rocks = 0L
        var j = 0
        var r = 0
        var lastCycleData = CycleData(0, 0, 0, 0, 0, 0)
        while (rocks < maxRocks) {
            if (r == 0) {
                val cycleData = CycleData(
                    rocks,
                    rocks - lastCycleData.rocks,
                    chamber.height,
                    chamber.height - lastCycleData.height,
                    r,
                    j
                )
                result.add(cycleData)
                lastCycleData = cycleData
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
        return result
    }

    class Chamber(
        val minX: Int = 0,
        val maxX: Int = 6
    ) {
        var minY = 0
        var savedHeight = 0L
        var blockCount = 0L

        val height get(): Long = savedHeight + minY

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
