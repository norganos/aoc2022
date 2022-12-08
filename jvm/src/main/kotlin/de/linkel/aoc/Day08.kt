package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton

@Singleton
class Day08: AbstractLinesAdventDay<Day08.Result>() {
    override val day = 8

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

        val visibleInside = grid
            .subList(1, rows - 1)
            .mapIndexed { r, row ->
                row
                    .subList(1, cols - 1)
                    .mapIndexed { c, tree ->
                        getLinesOfSight(grid, r+1, c+1)
                            .any { line ->
                                line.all { it < tree }
                            }
                    }
                    .map {
                        if (it) 1 else 0
                    }
                    .sum()
            }
            .sumOf { it }

        return Result(visibleInside + 2 * rows + 2 * cols - 4)
    }

    private fun getLinesOfSight(grid: List<List<Int>>, r: Int, c: Int): List<List<Int>> {
        return listOf(
            List(r) { i -> grid[i][c] },
            List(grid.size - r - 1) { i -> grid[r + i + 1][c] },
            List(c) { i -> grid[r][i] },
            List(grid[0].size - c - 1) { i -> grid[r][c + i + 1] },
        )
    }

    data class Result(
        val visible: Int
    ) {
        override fun toString(): String {
            return "visible from outside: $visible"
        }
    }
}
