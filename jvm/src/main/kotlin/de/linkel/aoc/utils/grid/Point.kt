package de.linkel.aoc.utils.grid

import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.sign

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

    operator fun rangeTo(p: Point): List<Point> {
        if (p == this) {
            return listOf(p)
        }
        val vector = p - this
        return if (vector.deltaY == 0) {
            (0 .. abs(vector.deltaX))
                .map { i -> this.copy(
                    x = x + (i) * vector.deltaX.sign
                )}
        } else if (vector.deltaX == 0) {
            (0 .. abs(vector.deltaY))
                .map { i -> this.copy(
                    y = y + (i) * vector.deltaY.sign
                )}
        } else {
            throw IllegalArgumentException("rangeTo only works in straight lines")
        }
    }

    override fun toString(): String {
        return "$x/$y"
    }
}
