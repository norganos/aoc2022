package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day21Test {
    val aocExample = """
        ...#
        .#..
        #...
        ....
...#.......#
........#...
..#....#....
..........#.
        ...#....
        .....#..
        .#......
        ......#.

10R5L5R10L4R5L5
          """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            6032,
            Day22(1).test(aocExample).value
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            5031,
            Day22(2).test(aocExample).value
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            64256,
            Day22(1).solve(emptyList()).value
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            0,
            Day22().solve(emptyList()).value
        )
    }
}
