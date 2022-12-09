package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import kotlin.math.abs

@Singleton
class Day09(
    @Suppress("MnInjectionPoints") @Value("1") val length: Int = 9
): AbstractLinesAdventDay<Day09.Result>() {
    override val day = 9

    override fun process(lines: Sequence<String>): Result {
        val rope = Rope(length)

        lines.forEach { line ->
            val tt = line.split(' ')
            val dir = Direction.valueOf(tt[0])
            repeat(tt[1].toInt()) {
                rope.moveHead(dir)
            }
        }

        return Result(length, rope.tailPoints.size)
    }

    class Rope(
        length: Int
    ) {
        private var knots: Array<Point> = List(length + 1) { _ -> Point(0, 0) }.toTypedArray()
        val tailPoints = mutableSetOf(Point(0, 0))

        fun moveHead(direction: Direction) {
            knots[0] = knots[0] + direction.vector

            for (i in 1 until knots.size) {
                val v = knots[i-1] - knots[i]
                if (v.distance >= 2) {
                    knots[i] = knots[i] + Vector(
                        deltaX = if (v.deltaX == 0) 0 else v.deltaX / abs(v.deltaX),
                        deltaY = if (v.deltaY == 0) 0 else v.deltaY / abs(v.deltaY)
                    )
                }
            }
            tailPoints.add(knots.last())
        }
    }

    enum class Direction(val vector: Vector) {
        U(Vector(0, -1)),
        L(Vector(-1, 0)),
        R(Vector(1, 0)),
        D(Vector(0, 1)),
    }

    data class Result(
        val length: Int,
        val tailTrailLength: Int
    ) {
        override fun toString(): String {
            return "tail of length $length touched $tailTrailLength points"
        }
    }
}
