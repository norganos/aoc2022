package de.linkel.aoc.utils.grid

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

    override fun toString(): String {
        return "$x/$y"
    }
}
