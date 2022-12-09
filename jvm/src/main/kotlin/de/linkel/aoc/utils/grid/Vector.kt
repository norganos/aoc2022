package de.linkel.aoc.utils.grid

import kotlin.math.abs
import kotlin.math.max

data class Vector(
    val deltaX: Int,
    val deltaY: Int
) {
    val distance get(): Int = max(abs(deltaX), abs(deltaY))

    override fun toString(): String {
        return "[${deltaX}/$deltaY]"
    }
}
