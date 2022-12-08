package de.linkel.aoc

interface AdventDay<T> {
    val day: Int
    fun solve(args: List<String>): T
}
