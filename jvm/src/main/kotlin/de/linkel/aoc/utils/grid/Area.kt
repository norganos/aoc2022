package de.linkel.aoc.utils.grid

data class Area(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
) {
    val origin = Point(x, y)
    val dimension = Dimension(width, height)

    operator fun contains(point: Point): Boolean {
        return point.x >= x && point.y >= y && point.x < x + width && point.y < y + height
    }

    fun extendTo(point: Point): Area = copy(
            x = if (point.x < x) point.x else x,
            y = if (point.y < y) point.y else y,
            width = if (point.x >= x + width) point.x - x + 1 else width,
            height = if (point.y >= y + height) point.y - y + 1 else height
        )

    override fun toString(): String {
        return "${width}x${height}@${x}/${y}"
    }
}
