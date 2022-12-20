package de.linkel.aoc.utils.space.discrete

import kotlin.math.*

data class Vector3d(
    val deltaX: Int,
    val deltaY: Int,
    val deltaZ: Int
) {
    val length: Int = round(sqrt(deltaX.toDouble().pow(2) + deltaY.toDouble().pow(2) + deltaZ.toDouble().pow(2))).toInt()
    val manhattenDistance: Int = abs(deltaX) + abs(deltaY) + abs(deltaZ)
    private var cachedNormalized: Vector3d? = null
    val normalized get(): Vector3d {
        if (cachedNormalized == null) {
            val min = min(deltaX, min(deltaY, deltaZ))
            var gcd = 1
            for (f in (min downTo 2)) {
                if (deltaX % f == 0 && deltaY % f == 0 && deltaZ % f == 0) {
                    gcd = f
                    break
                }
            }
            if (gcd != 1) {
                cachedNormalized = this / gcd
            }
            cachedNormalized = this
        }
        return cachedNormalized!!
    }

    override fun toString(): String {
        return "[$deltaX/$deltaY/$deltaZ]"
    }

    infix fun cross(v: Vector3d): Vector3d {
        return Vector3d(
            deltaX = deltaY * v.deltaZ - deltaZ * v.deltaY,
            deltaY = deltaX * v.deltaZ - deltaZ * v.deltaX,
            deltaZ = deltaX * v.deltaY - deltaY * v.deltaX,
        )
    }

    operator fun times(a: Int): Vector3d {
        return Vector3d(
            deltaX = deltaX * a,
            deltaY = deltaY * a,
            deltaZ = deltaZ * a,
        )
    }

    operator fun contains(v: Vector3d): Boolean {
        if (this == v || this == -v) { return true }
        val tuples = listOf(
                Pair(deltaX, v.deltaX),
                Pair(deltaY, v.deltaY),
                Pair(deltaZ, v.deltaZ),
            )
            .filter { it.first != 0 || it.second != 0 }
        if (tuples.isEmpty()) {
            return true
        }
        if (tuples.any { it.first == 0 || it.second == 0}) {
            return false
        }
        if (tuples.size == 1) {
            return true
        }
        return (1 until tuples.size).all { i ->
            tuples[i].first * tuples[0].second == tuples[i].second * tuples[0].first
        }
    }

    operator fun div(a: Int): Vector3d {
        return Vector3d(
            deltaX = deltaX / a,
            deltaY = deltaY / a,
            deltaZ = deltaZ / a,
        )
    }

    operator fun unaryMinus(): Vector3d {
        return Vector3d(
            deltaX = - deltaX,
            deltaY = - deltaY,
            deltaZ = - deltaZ,
        )
    }

    @Suppress("unused")
    infix fun scalar(v: Vector3d): Int {
        return deltaX * v.deltaX + deltaY * v.deltaY + deltaZ * v.deltaZ
    }
}
