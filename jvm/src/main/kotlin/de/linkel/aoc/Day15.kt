package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.lang.IllegalArgumentException
import java.util.regex.Pattern
import kotlin.math.abs

@Singleton
class Day15(
    @Suppress("MnInjectionPoints") @Value("2000000") val row: Int = 2000000,
): AbstractLinesAdventDay<Day15.Result>() {
    override val day = 15

    override fun process(lines: Sequence<String>): Result {
        val map = Grid<Type>()
        val pattern = Pattern.compile(".*sensor.*x\\s*=\\s*(-?\\d+).*y\\s*=\\s*(-?\\d+).*beacon.*x\\s*=\\s*(-?\\d+).*y\\s*=\\s*(-?\\d+).*")
        lines.forEach { line ->
            val match = pattern.matcher(line.lowercase())
            if (match.matches()) {
                val sp = Point(match.group(1).toInt(), match.group(2).toInt())
                val bp = Point(match.group(3).toInt(), match.group(4).toInt())
//                println("sensor: $sp beacon $bp")
                map.stretchTo(bp)
                map.stretchTo(sp)
                map[bp] = Beacon()
                map[sp] = Sensor(bp)
            } else if (line.isNotBlank()) {
                throw IllegalArgumentException("couldn't parse line '$line'")
            }
        }
        map.crop()
        val possibleX = mutableSetOf<Int>()
        val box = map.getDataBoundingBox()
        possibleX.addAll(box.x until (box.x + box.width))
        map.getAllData()
            .forEach {
                if (it.point.y == row) {
                    possibleX.remove(it.point.x)
                }
                if (it.data is Sensor) {
                    val s2b = (it.data.beacon - it.point)
                    val s2d = abs(row - it.point.y)
                    if (s2d <= s2b.manhattenDistance) {
                        val dy = row - it.point.y
                        val mdx = s2b.manhattenDistance - s2d
                        ((-mdx)..mdx).forEach { dx ->
                            possibleX.remove(it.point.x + dx)
                            val p = it.point + Vector(dx, dy)
                            map.stretchTo(p)
                            if (map[p] == null) {
//                                println("no beacon at $p")
                                map[p] = NoBeacon()
                            }
                        }
                    }
                }

//                val distance = ((it.data as Sensor).beacon - it.point).manhattenDistance
//                if (it.point.y < row && it.point.y + distance >= row) {
//                    (1 .. distance).forEach { d ->
//                        (0..distance).forEach { a ->
//                            val dy = d - a
//                            if (it.point.y + dy == row) {
//                                listOf(a, -a).forEach { dx ->
//                                    val p = it.point + Vector(dx, dy)
//                                    if (map[p] == null) {
//                                        map[p] = NoBeacon()
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else if (it.point.y > row && it.point.y - distance <= row) {
//                    (1 .. distance).forEach { d ->
//                        (0..distance).forEach { a ->
//                            val dy = -d + a
//                            if (it.point.y + dy == row) {
//                                listOf(a, -a).forEach { dx ->
//                                    val p = it.point + Vector(dx, dy)
//                                    if (map[p] == null) {
//                                        map[p] = NoBeacon()
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } else if (it.point.y == row) {
//                    val dy = 0
//                    (1 .. distance).forEach { a ->
//                        listOf(a, -a).forEach { dx ->
//                            val p = it.point + Vector(dx, dy)
//                            if (map[p] == null) {
//                                map[p] = NoBeacon()
//                            }
//                        }
//                    }
//                }
            }

        val noBeacons = map.getRowData(row).filter { it.data is NoBeacon }
//        println(noBeacons.map { it.point }.joinToString(", "))
        return Result(
            noBeacons.size
        )
    }


    data class Result(
        val value: Int
    ) {
        override fun toString(): String {
            return "solution: $value"
        }
    }

    interface Type
    class Beacon: Type
    class NoBeacon: Type
    class Sensor(val beacon: Point): Type
}
