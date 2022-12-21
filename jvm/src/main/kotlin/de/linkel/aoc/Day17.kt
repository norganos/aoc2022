package de.linkel.aoc

import de.linkel.aoc.base.AbstractFileAdventDay
import de.linkel.aoc.utils.grid.Dimension
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import de.linkel.aoc.utils.readers.ReaderSequence
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.io.BufferedReader
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.max
import kotlin.math.min

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
        val cycleInfo = cycleDetector(jetPattern, c, min(blockCount, 0L + jetPattern.size * rockPatterns.size))
            .runningFold(CycleDetectionAggregate(jetPattern.size)) { buffer, status ->
                buffer + status
            }
            .firstNotNullOfOrNull { it.detectCycle() }

        val chamber = Chamber()
        return if (cycleInfo != null && blockCount > cycleInfo.beforeCycle.rocks + cycleInfo.cycle.rocks) {
            println("found a cycle with length ${cycleInfo.cycle.rocks}")
            rockCycle(jetPattern, chamber, cycleInfo.beforeCycle.rocks)
            val cycleCount = (blockCount - cycleInfo.beforeCycle.rocks) / cycleInfo.cycle.rocks
            val remainder = (blockCount - cycleInfo.beforeCycle.rocks) % cycleInfo.cycle.rocks
            rockCycle(jetPattern, chamber, remainder, cycleInfo.beforeCycle.jetIndex)

            Result(cycleCount * cycleInfo.cycle.height + chamber.height)
        } else {
            if (cycleInfo == null) {
                println("did not find a cycle :-/ have to simulate all $blockCount rocks")
            } else {
                println("found a cycle that starts after $blockCount rocks")
            }
            rockCycle(jetPattern, chamber, blockCount)
            Result(chamber.height)
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
                var nextRock = rock + jetPatterns[j]
                j = (j + 1) % jetPatterns.size
                rock = if (nextRock in chamber && !chamber.blocks(nextRock)) nextRock else rock
                nextRock = rock + down
                if (chamber.blocks(nextRock)) {
                    chamber.put(rock)
                    break
                } else {
                    rock = nextRock
                }
            }
            rocks++
        }
        return RockPile(rocks, chamber.height - startHeight, j)
    }

    class CycleDetectionAggregate(
        val bufferSize: Int
    ) {
        val buffer = mutableListOf<CycleData>()
        private val bufferSet = mutableSetOf<CycleDataPattern>()
        var lastData = CycleData()
            private set
        operator fun plus(data: CycleData): CycleDetectionAggregate {
            if (buffer.size == bufferSize) {
                val old = buffer.removeAt(0)
                bufferSet.remove(old.toPattern())
            }
            buffer.add(lastData)
            bufferSet.add(lastData.toPattern())
            lastData = data
            return this
        }

        fun detectCycle(): CycleInfo? {
            val pattern = lastData.toPattern()
            return if (bufferSet.contains(pattern)) {
                val cycleStart = buffer.indexOfFirst { it.toPattern() == pattern }
                val cycleElements = buffer.subList(cycleStart, buffer.size)
                CycleInfo(
                    beforeCycle = RockPile(
                        rocks = buffer[cycleStart].rocks,
                        height = buffer[cycleStart].height,
                        jetIndex = buffer[cycleStart].jetIndex
                    ),
                    cycle = RockPile(
                        rocks = cycleElements.sumOf { it.rocksDelta },
                        height = cycleElements.sumOf { it.heightDelta },
                        jetIndex = buffer.last().jetIndex
                    )
                )
            } else null
        }
    }

    data class CycleData(
        val rocks: Long = 0,
        val rocksDelta: Long = 0,
        val height: Long = 0,
        val heightDelta: Long = 0,
        val rockIndex: Int = 0,
        val jetIndex: Int = 0,
        val surface: ByteArray = ByteArray(0)
    ) {
        fun toPattern(): CycleDataPattern = CycleDataPattern(rocksDelta, heightDelta, rockIndex, jetIndex, surface)
    }
    data class CycleDataPattern(
        val rocksDelta: Long,
        val heightDelta: Long,
        val rockIndex: Int,
        val jetIndex: Int,
        val surface: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CycleDataPattern

            if (rocksDelta != other.rocksDelta) return false
            if (heightDelta != other.heightDelta) return false
            if (rockIndex != other.rockIndex) return false
            if (jetIndex != other.jetIndex) return false
            if (!surface.contentEquals(other.surface)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = rocksDelta.hashCode()
            result = 31 * result + heightDelta.hashCode()
            result = 31 * result + rockIndex
            result = 31 * result + jetIndex
            result = 31 * result + surface.contentHashCode()
            return result
        }
    }

    data class RockPile(
        val rocks: Long,
        val height: Long,
        val jetIndex: Int
    )
    data class CycleInfo(
        val beforeCycle: RockPile,
        val cycle: RockPile
    )
    fun cycleDetector(jetPatterns: List<Vector>, chamber: Chamber, maxRocks: Long): Sequence<CycleData> {
        return sequence {
            var rocks = 0L
            var j = 0
            var r = 0
            var lastCycleData = CycleData()
            while (rocks < maxRocks) {
                if (r == 0) {
                    val cycleData = CycleData(
                        rocks,
                        rocks - lastCycleData.rocks,
                        chamber.height,
                        chamber.height - lastCycleData.height,
                        r,
                        j,
                        chamber.hash
                    )
                    lastCycleData = cycleData
                    yield(cycleData)
                }
                var rock = rockPatterns[r].plus(Vector(0, chamber.minY + 4))
                r = (r + 1) % rockPatterns.size
                while (true) {
                    var nextRock = rock + jetPatterns[j]
                    j = (j + 1) % jetPatterns.size
                    rock = if (nextRock in chamber && !chamber.blocks(nextRock)) nextRock else rock
                    nextRock = rock + down
                    if (chamber.blocks(nextRock)) {
                        chamber.put(rock)
                        break
                    } else {
                        rock = nextRock
                    }
                }
                rocks++
            }
        }
    }

    class Chamber(
        val minX: Int = 0,
        val maxX: Int = 6
    ) {
        val hashSize = 16
        var minY = 0
        var savedHeight = 0L
        var blockCount = 0L

        val height get(): Long = savedHeight + minY

        var topRocks: List<Shape> = emptyList()
        var hash: ByteArray = ByteArray(hashSize) { _ -> 0.toByte() }

        operator fun contains(s: Shape): Boolean {
            return s.points.all { it.x in minX..maxX }
        }

        fun blocks(s: Shape): Boolean {
            return topRocks.any { it.intersects(s) } || s.points.any { it.y == 0 }
        }

        fun put(s: Shape) {
            blockCount++
            topRocks = (topRocks + s)
                .sortedByDescending { r -> r.points.maxOf { it.y } }
                .take(50)
            minY = topRocks.maxOf { r -> r.points.maxOf { it.y } }
            if (minY > 10_000) {
                savedHeight += 10_000
                val transform = Vector(0,-10_000)
                topRocks = topRocks.map {
                    it + transform
                }
            }

            hash = ByteArray(hashSize) { i ->
                topRocks
                    .flatMap { it.points }
                    .filter { it.y == minY - hashSize + i }
                    .fold( 0) { b, p ->
                        b or (1 shl p.x)
                    }
                    .toByte()
            }
        }

        @Suppress("unused")
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
            setOf(
                Point(2, 0),
                Point(3, 0),
                Point(4, 0),
                Point(5, 0)
            )
        ),
        Shape(
            setOf(
                Point(3, 2),
                Point(2, 1),
                Point(3, 1),
                Point(4, 1),
                Point(3, 0)
            )
        ),
        Shape(
            setOf(
                Point(4, 2),
                Point(4, 1),
                Point(2, 0),
                Point(3, 0),
                Point(4, 0)
            )
        ),
        Shape(
            setOf(
                Point(2, 3),
                Point(2, 2),
                Point(2, 1),
                Point(2, 0)
            )
        ),
        Shape(
            setOf(
                Point(2, 1),
                Point(3, 1),
                Point(2, 0),
                Point(3, 0)
            )
        )
    )

    data class Shape(
        val points: Set<Point>
    ) {
        val boundingBox = Dimension(
            points.maxOf { it.x } - points.minOf { it.x } + 1,
            points.maxOf { it.y } - points.minOf { it.y } + 1
        )
        val bottomY = points.minOf { it.y }
        val bytes: ByteArray

        // this code here because we know our x will always be between 0 and 7
        init {
            bytes = ByteArray(boundingBox.height) { i ->
                points
                    .filter { it.y == bottomY + i }
                    .fold( 0) { b, p ->
                        b or (1 shl p.x)
                    }
                    .toByte()
            }
        }

        operator fun plus(v: Vector): Shape {
            return copy(
                points = points.map { it + v }.toSet()
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
