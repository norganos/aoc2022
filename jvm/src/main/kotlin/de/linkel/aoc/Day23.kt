package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.DataPoint
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton


@Singleton
class Day23(
    @Suppress("MnInjectionPoints") @Value("-1") val rounds: Int = -1,
): AbstractLinesAdventDay<Day23.Result>() {
    override val day = 23

    val N = Vector(0, -1)
    val NE = Vector(1, -1)
    val E = Vector(1, 0)
    val SE = Vector(1, 1)
    val S = Vector(0, 1)
    val SW = Vector(-1, 1)
    val W = Vector(-1, 0)
    val NW = Vector(-1, -1)

    override fun process(lines: Sequence<String>): Result {
        val grid = Grid.parse(lines) { pos, c ->
            if (c == '#') Elf() else null
        }

        val around = listOf(N, NE, E, SE, S, SW, W, NW)

        val searchOrder = listOf(
            MoveStrategy(listOf(NW, N, NE), N),
            MoveStrategy(listOf(SW, S, SE), S),
            MoveStrategy(listOf(NW, W, SW), W),
            MoveStrategy(listOf(NE, E, SE), E),
        )
        var i = 0
        while(true) {
            grid.stretchTo(grid.area.northWest + NW)
            grid.stretchTo(grid.area.southEast + SE)
            val moveTo = mutableMapOf<Point, List<DataPoint<Elf>>>()
            for (dp in grid.getAllData()) {
                if (around.all { grid[dp.point + it] == null }) {
                    continue
                }
                (0..3)
                    .map { searchOrder[(i + it) % 4] }
                    .firstOrNull { s -> s.lookIn.all { grid[dp.point + it] == null } }
                    .let {
                        if (it != null) {
                            moveTo[dp.point + it.moveTo] = (moveTo[dp.point + it.moveTo] ?: emptyList()) + dp
                        }
                    }
            }
            var moved = false
            for (move in moveTo) {
                if (move.value.size > 1) {
                    continue
                }
                moved = true
                val dp = move.value.first()
                grid[dp.point] = null
                grid[move.key] = dp.data
            }
            grid.crop()
            i++
            if (!moved || (rounds > 0 && i == rounds)) {
                break
            }
        }

        return Result(
            grid.area.width * grid.area.height - grid.getAllData().size,
            i
        )
    }

    class Elf {}

    data class MoveStrategy(
        val lookIn: List<Vector>,
        val moveTo: Vector
    )

    data class Result(
        val freePoints: Int,
        val rounds: Int
    ) {
        override fun toString(): String {
            return "$freePoints after $rounds rounds"
        }
    }
}
