package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton

@Singleton
class Day08: AbstractLinesAdventDay<Day08.Result>() {
    override val day = 8

    private fun Collection<Int>.product(): Int = this.fold(1) { p, v -> p * v }

    override fun process(lines: Sequence<String>): Result {
        val grid = lines
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { line ->
                line
                    .toCharArray()
                    .map { it.toString().toInt() }
                    .toList()
            }
            .toList()
        assert(grid.isNotEmpty())
        val rows = grid.size
        val cols = grid[0].size
        assert(grid.all { it.size == cols })

        val insideTrees = grid
            .subList(1, rows - 1)
            .flatMapIndexed { r, row ->
                row
                    .subList(1, cols - 1)
                    .mapIndexed { c, tree ->
                        val linesOfSight = getLinesOfSight(grid, r+1, c+1)
                        InsideTreeSpec(
                            r + 1,
                            c + 1,
                            tree,
                            linesOfSight
                                .any { los ->
                                    los.all { it < tree }
                                },
                            linesOfSight
                                .map { los ->
                                    los
                                        .indexOfFirst { it >= tree }
                                        .let { if (it == -1) los.size else it + 1 }
                                }
                                .product()
                        )
                    }
            }
            .toList()
        val visibleInside = insideTrees
            .sumOf { it.visibleFromOutsideScore }
        val highestScenicScore = insideTrees.maxOf { it.scenicScore }
        println("best score: ${insideTrees.maxBy { it.scenicScore }}")

        return Result(visibleInside + 2 * rows + 2 * cols - 4, highestScenicScore)
    }

    private fun getLinesOfSight(grid: List<List<Int>>, r: Int, c: Int): List<List<Int>> {
        return listOf(
            List(r) { i -> grid[i][c] }.reversed(),
            List(grid.size - r - 1) { i -> grid[r + i + 1][c] },
            List(c) { i -> grid[r][i] }.reversed(),
            List(grid[0].size - c - 1) { i -> grid[r][c + i + 1] },
        )
    }

    data class InsideTreeSpec(
        val r: Int,
        val c: Int,
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
