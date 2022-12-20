package de.linkel.aoc.utils.space.continuous

import java.math.BigDecimal

data class Point3d(
    val x: BigDecimal,
    val y: BigDecimal,
    val z: BigDecimal
): Comparable<Point3d> {
    operator fun plus(v: Vector3d): Point3d {
        return copy(
            x = x + v.deltaX,
            y = y + v.deltaY,
            z = z + v.deltaZ,
        )
    }
    operator fun minus(p: Point3d): Vector3d {
        return Vector3d(
            deltaX = x - p.x,
            deltaY = y - p.y,
            deltaZ = z - p.z
        )
    }

    override fun compareTo(other: Point3d): Int {
        return x.compareTo(other.x)
            .let { if (it == 0) y.compareTo(other.y) else it }
            .let { if (it == 0) z.compareTo(other.z) else it }
    }

    override fun toString(): String {
        return "$x/$y/$z"
    }
}
