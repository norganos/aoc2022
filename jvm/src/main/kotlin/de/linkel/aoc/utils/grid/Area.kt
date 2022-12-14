package de.linkel.aoc.utils.grid

data class Area(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {

    operator fun contains(point: Point): Boolean {
        return point.x >= x && point.y >= y && point.x < x + width && point.y < y + height
    }

    override fun toString(): String {
        return "${width}x${height}@${x}/${y}"
    }
}
