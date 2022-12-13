package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day02Test {
    val aocExample = """
A Y
B X
C Z
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            15,
            Day02().test(aocExample).part1
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            12,
            Day02().test(aocExample).part2
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            13682,
            Day02().solve(emptyList()).part1
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            12881,
            Day02().solve(emptyList()).part2
        )
    }
}
