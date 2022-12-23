package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day23Test {
    val aocExample = """
....#..
..###.#
#...#.#
.#...##
#.###..
##.#.##
.#..#..
          """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            110,
            Day23(10).test(aocExample).freePoints
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            20,
            Day23().test(aocExample).rounds
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            4075,
            Day23(10).solve(emptyList()).freePoints
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            950,
            Day23().solve(emptyList()).rounds
        )
    }
}
