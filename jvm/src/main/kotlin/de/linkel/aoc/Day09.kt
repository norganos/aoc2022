package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton
import kotlin.math.abs
import kotlin.math.max

@Singleton
class Day09: AbstractLinesAdventDay<Day09.Result>() {
    override val day = 9

    override fun process(lines: Sequence<String>): Result {
        val hStart = Point(0, 0)
        val tStart = Point(0, 0)
        val rope = Rope(hStart, tStart)

        lines.forEach { line ->
            val tt = line.split(' ')
            val dir = Direction.valueOf(tt[0])
            repeat(tt[1].toInt()) {
                rope.moveHead(dir)
            }
        }

        return Result(rope.tailPoints.size)
    }

    class Rope(
        headStart :Point,
        tailStart: Point
    ) {
        private var tail: Point = tailStart
        private var head: Point = headStart
        val tailPoints = mutableSetOf<Point>(tailStart)

        fun moveHead(direction: Direction) {
            head += direction.vector
            val t2h = head - tail
            if (t2h.distance >= 2) {
                tail += Vector(
                    deltaX = if (t2h.deltaX == 0) 0 else t2h.deltaX / abs(t2h.deltaX),
                    deltaY = if (t2h.deltaY == 0) 0 else t2h.deltaY / abs(t2h.deltaY)
                )
                tailPoints.add(tail)
            }
        }
    }

    data class Point(
        val x: Int,
        val y: Int
    ) {
        operator fun plus(v: Vector): Point {
            return copy(
                x = x + v.deltaX,
                y = y + v.deltaY
            )
        }
        operator fun minus(p: Point): Vector {
            return Vector(
                deltaX = x - p.x,
                deltaY = y - p.y
            )
        }
    }

    data class Vector(
        val deltaX: Int,
        val deltaY: Int
    ) {
        val distance get(): Int = max(abs(deltaX), abs(deltaY))
    }

    enum class Direction(val vector: Vector) {
        U(Vector(0, -1)),
        L(Vector(-1, 0)),
        R(Vector(1, 0)),
        D(Vector(0, 1)),
    }

    data class Result(
        val tailTrailLength: Int
    ) {
        override fun toString(): String {
            return "tail touched $tailTrailLength points"
        }
    }
}
