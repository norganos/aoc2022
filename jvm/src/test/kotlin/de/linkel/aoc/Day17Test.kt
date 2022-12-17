package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day17Test {
    val aocExample = """
>>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            3068,
            Day17(2022).test(aocExample).height
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            1514285714288L,
            Day17().test(aocExample).height
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            3173,
            Day17(2022).solve(emptyList()).height
        )
    }

//    @Test
//    fun `solution part 2`() {
//        assertEquals(
//            2838,
//            Day16().solve(emptyList()).sumReleasedPressure2
//        )
//    }
}
