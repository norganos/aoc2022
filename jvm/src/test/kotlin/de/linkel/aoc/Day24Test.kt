package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day24Test {
    val aocExample = """
#.######
#>>.<^<#
#.<..<<#
#>v.><>#
#<^v^^>#
######.#
          """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            18,
            Day24().test(aocExample).part1
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            54,
            Day24().test(aocExample).part2
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            240,
            Day24().solve(emptyList()).part1
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            717,
            Day24().solve(emptyList()).part2
        )
    }
}
