package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton

@Singleton
class Day18(): AbstractLinesAdventDay<Day18.Result>() {
    override val day = 18

    override fun process(lines: Sequence<String>): Result {
        val voxels = lines
            .map { it.split(",") }
            .map { it.map { i -> i.toInt() } }
            .map { Voxel(it[0], it[1], it[2]) }
            .toSet()
        val surface = voxels.sumOf { voxel ->
            voxel.neighbours
                .map {
                    if (voxels.contains(it)) 0 else 1
                }
                .sum()
        }

        return Result(surface, surface)
    }

    data class Voxel(
        val x: Int,
        val y: Int,
        val z: Int
    ) {
        val neighbours get(): Collection<Voxel> {
            return listOf(
                copy(x = x + 1),
                copy(x = x - 1),
                copy(y = y + 1),
                copy(y = y - 1),
                copy(z = z + 1),
                copy(z = z - 1)
            )
        }
    }

    data class Result(
        val surface: Int,
        val exteriorSurface: Int
    ) {
        override fun toString(): String {
            return "surface area is $surface, exterior surface is $exteriorSurface"
        }
    }
}
