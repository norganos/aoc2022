package de.linkel.aoc.utils.space.discrete

import java.lang.IllegalArgumentException

@Suppress("unused")
class Polygon(points: Collection<Point3d>) {
    val corners = points.sorted().distinct().toList()
    val normalVector: Vector3d

    init {
        if (corners.size < 3) {
            throw IllegalArgumentException("need at least 3 different points, got ${points.joinToString(", ")}")
        }
        normalVector = (corners[1] - corners[0]) cross (corners[2] - corners[0])
        (2 until corners.size).forEach { n ->
            val v = (corners[n-1] - corners[n]) cross (corners[n-2] - corners[n])
            if (v !in normalVector) {
                throw IllegalArgumentException("points ${n-2}..${n} $v are not in the same plane as points 0..2 $normalVector (points ${corners.joinToString(", ")}}")
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Polygon

        if (corners != other.corners) return false

        return true
    }

    fun touches(other: Polygon): Boolean {
        //TODO: eigtl brauchen wir eine konkave sortierung der eckpunkte (damit keine diagonalen verglichen werden)
        return corners.intersect(other.corners.toSet()).size >= 2
    }

    override fun hashCode(): Int {
        return corners.hashCode()
    }

    override fun toString(): String {
        return "Panel $corners"
    }
}
