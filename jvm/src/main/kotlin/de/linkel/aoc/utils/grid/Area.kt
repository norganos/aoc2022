package de.linkel.aoc.utils.grid

import kotlin.math.max
import kotlin.math.min

data class Area(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {
    val origin = Point(x, y)
    val northWest get(): Point = Point(x, y)
    @Suppress("unused")
    val northEast get(): Point = Point(x + width - 1, y)
    val southEast get(): Point = Point(x + width - 1, y + height - 1)
    @Suppress("unused")
    val southWest get(): Point = Point(x, y + height - 1)
    val dimension = Dimension(width, height)

    operator fun contains(point: Point): Boolean {
        return point.x >= x && point.y >= y && point.x < x + width && point.y < y + height
    }

    fun extendTo(point: Point): Area {
        val x = min(this.x, point.x)
        val y = min(this.y, point.y)
        val w = max(this.x + width, point.x + 1) - x
        val h = max(this.y + height, point.y + 1) - y
        return copy(
            x = x,
            y = y,
            width = w,
            height = h
        )
    }

    override fun toString(): String {
        return "${width}x${height}@${x}/${y}"
    }

    operator fun plus(vector: Vector): Area {
        return copy(
            x = x + vector.deltaX,
            y = y + vector.deltaY
        )
    }
}
