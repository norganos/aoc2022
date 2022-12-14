package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day14Test {
    val aocExample = """
498,4 -> 498,6 -> 496,6
503,4 -> 502,4 -> 502,9 -> 494,9
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            24,
            Day14().test(aocExample).sandUnits1
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            93,
            Day14().test(aocExample).sandUnits2
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            625,
            Day14().solve(emptyList()).sandUnits1
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            25193,
            Day14().solve(emptyList()).sandUnits2
        )
    }
}
