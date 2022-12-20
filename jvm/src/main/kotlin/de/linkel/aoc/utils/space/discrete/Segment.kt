package de.linkel.aoc.utils.space.discrete

import java.lang.IllegalArgumentException

class Segment(a: Point3d, b: Point3d): Comparable<Segment> {
    val corners = listOf(a, b).sorted().distinct().toList()

    init {
        if (corners.size != 2) {
            throw IllegalArgumentException("expected 2 different points, got ${a}, $b")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Segment

        if (corners != other.corners) return false

        return true
    }

    override fun compareTo(other: Segment): Int {
        return corners[0].compareTo(other.corners[0])
            .let { if (it == 0) corners[1].compareTo(other.corners[1]) else it }

    }

    override fun hashCode(): Int {
        return corners.hashCode()
    }

    override fun toString(): String {
        return "Panel $corners"
    }
}
