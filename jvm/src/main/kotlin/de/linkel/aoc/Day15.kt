package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Area
import de.linkel.aoc.utils.grid.Point
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.max

@Singleton
class Day15(
    @Suppress("MnInjectionPoints") @Value("2000000") val row: Int = 2000000,
    @Suppress("MnInjectionPoints") @Value("4000000") val boxSize: Int = 4000000,
): AbstractLinesAdventDay<Day15.Result>() {
    override val day = 15

    override fun process(lines: Sequence<String>): Result {
        val distressBox = Area(0,0,boxSize, boxSize)
        val pattern = Pattern.compile(".*sensor.*x\\s*=\\s*(-?\\d+).*y\\s*=\\s*(-?\\d+).*beacon.*x\\s*=\\s*(-?\\d+).*y\\s*=\\s*(-?\\d+).*")
        val noBeacons = mutableSetOf<Int>()
        val sensors = lines.mapNotNull { line ->
            val match = pattern.matcher(line.lowercase())
            if (match.matches()) {
                val sp = Point(match.group(1).toInt(), match.group(2).toInt())
                val bp = Point(match.group(3).toInt(), match.group(4).toInt())
                val s2b = (bp - sp)
                val s2d = abs(row - sp.y)
                if (s2d <= s2b.manhattenDistance) {
                    val mdx = s2b.manhattenDistance - s2d
                    ((-mdx)..mdx).forEach { dx ->
                        noBeacons.add(sp.x + dx)
                    }
                }
                SensorBeaconPair(sp,bp)
            } else if (line.isNotBlank()) {
                throw IllegalArgumentException("couldn't parse line '$line'")
            } else {
                null
            }
        }.toList()

        sensors.flatMap {
            listOf(it.sensor, it.beacon)
        }.forEach {
            if (it.y == row) {
                noBeacons.remove(it.x)
            }
        }

        var x = distressBox.origin.x
        var y = distressBox.origin.y
        var distress: Point? = null
        while (distress == null) {
            val conflict = sensors.firstOrNull {
                abs(x - it.sensor.x) + abs(y - it.sensor.y) <= it.manhattenDistance
            }
            if (conflict == null) {
                distress = Point(x,y)
                break
            } else {
                x = max(x+1, conflict.sensor.x + conflict.manhattenDistance - abs(conflict.sensor.y - y))
                if (x > distressBox.x + distressBox.width) {
                    x = distressBox.x
                    y++
                    if (y > distressBox.y + distressBox.height) {
                        break
                    }
                }
            }
        }

        return Result(
            noBeacons.size,
            distress!!.x * 4000000L + distress.y
        )
    }


    data class Result(
        val value: Int,
        val frequency: Long
    ) {
        override fun toString(): String {
            return "in row: $value, frequency: $frequency"
        }
    }

    data class SensorBeaconPair(
        val sensor: Point,
        val beacon: Point
    ) {
        val manhattenDistance = (sensor - beacon).manhattenDistance
    }
}
