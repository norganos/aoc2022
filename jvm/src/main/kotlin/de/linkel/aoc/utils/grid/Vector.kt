package de.linkel.aoc.utils.grid

import kotlin.math.abs
import kotlin.math.max

data class Vector(
    val deltaX: Int,
    val deltaY: Int
) {
    val length: Int = abs(deltaX) + abs(deltaY)
    val manhattenDistance = abs(deltaX) + abs(deltaY)
    val maximumAxisDistance get(): Int = max(abs(deltaX), abs(deltaY))

    operator fun times(factor: Int): Vector {
        return Vector(factor * deltaX, factor * deltaY)
    }
    operator fun plus(other: Vector): Vector {
        return Vector(other.deltaX * deltaX, other.deltaY * deltaY)
    }

    override fun toString(): String {
        return "[${deltaX}/$deltaY]"
    }
}
