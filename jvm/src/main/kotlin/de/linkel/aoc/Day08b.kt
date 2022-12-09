package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point

class Day08b: AbstractLinesAdventDay<Day08b.Result>() {
    override val day = 8

    private fun Collection<Int>.product(): Int = this.fold(1) { p, v -> p * v }

    override fun process(lines: Sequence<String>): Result {
        val treeGrid = Grid<Int>()
            .let { grid ->
            lines
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEachIndexed { y, line ->
                    grid.resize(grid.width, y+1)
                    val chars = line
                        .toCharArray()
                    if (chars.size > grid.width) {
                        grid.resize(chars.size, grid.height)
                    }
                    chars
                        .forEachIndexed { x, c ->
                            grid[Point(x, y)] = c.toString().toInt()
                        }
                }
            grid.transform { pos, height ->
                if (pos.x == 0 || pos.y == 0 || pos.x == grid.width - 1 || pos.y == grid.height - 1) {
                    InsideTreeSpec(height, true, 0)
                } else {
                    val beams = grid.getBeams(pos)
                    InsideTreeSpec(
                        height,
                        beams
                            .any { beam ->
                                beam.all { it.data < height }
                            },
                        beams
                            .map { beam ->
                                beam
                                    .indexOfFirst { it.data >= height }
                                    .let { if (it == -1) beam.size else it + 1 }
                            }
                            .product()
                    )
                }
            }
        }

        val visibleFromOutside = treeGrid
            .filterData { _, tree -> tree.visibleFromOutside }
            .sumOf { it.data.visibleFromOutsideScore }
        val highestScenicScore = treeGrid
            .filterData { _, tree -> tree.scenicScore > 0 }
            .maxOf { it.data.scenicScore }

        return Result(visibleFromOutside, highestScenicScore)
    }

    data class InsideTreeSpec(
        val height: Int,
        val visibleFromOutside: Boolean,
        val scenicScore: Int
    ) {
        val visibleFromOutsideScore: Int = if (visibleFromOutside) 1 else 0
    }

    data class Result(
        val visible: Int,
        val highestScenicScore: Int
    ) {
        override fun toString(): String {
            return "visible from outside: $visible, highest scenic score: $highestScenicScore"
        }
    }
}
