package de.linkel.aoc.utils.space.continuous

import java.math.BigDecimal
import java.math.MathContext

data class Vector3d(
    val deltaX: BigDecimal,
    val deltaY: BigDecimal,
    val deltaZ: BigDecimal
) {
    companion object {
        private val mc = MathContext.DECIMAL64
    }

    val length: BigDecimal = (deltaX.pow(2) + deltaY.pow(2) + deltaZ.pow(2)).sqrt(mc)
    @Suppress("unused")
    val normalized get(): Vector3d = if (length.compareTo(BigDecimal.ONE) != 0) this / length else this

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

    operator fun times(a: BigDecimal): Vector3d {
        return Vector3d(
            deltaX = deltaX * a,
            deltaY = deltaY * a,
            deltaZ = deltaZ * a,
        )
    }

    operator fun contains(v: Vector3d): Boolean {
        if (this == v || this == -v) { return true }
        if (v.length.compareTo(BigDecimal.ZERO) == 0 && v.length.compareTo(BigDecimal.ZERO) == 0) { return true }
        if (this.deltaX.compareTo(BigDecimal.ZERO) != 0) {
            if (this.deltaX.compareTo(BigDecimal.ZERO) == 0) {
                return false
            }
            return this.deltaY / this.deltaX == v.deltaY / v.deltaX && this.deltaZ / this.deltaX == v.deltaZ / v.deltaX
        } else if (this.deltaY.compareTo(BigDecimal.ZERO) != 0) {
            if (this.deltaY.compareTo(BigDecimal.ZERO) == 0) {
                return false
            }
            return this.deltaX / this.deltaY == v.deltaX / v.deltaY && this.deltaZ / this.deltaY == v.deltaZ / v.deltaY
        } else if (this.deltaZ.compareTo(BigDecimal.ZERO) != 0) {
            if (this.deltaZ.compareTo(BigDecimal.ZERO) == 0) {
                return false
            }
            return this.deltaX / this.deltaZ == v.deltaX / v.deltaZ && this.deltaY / this.deltaZ == v.deltaY / v.deltaZ
        }
        return false
    }

    operator fun div(a: BigDecimal): Vector3d {
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
    infix fun scalar(v: Vector3d): BigDecimal {
        return deltaX * v.deltaX + deltaY * v.deltaY + deltaZ * v.deltaZ
    }
}
