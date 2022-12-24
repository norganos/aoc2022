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
            Day24().test(aocExample).value
        )
    }

//    @Test
//    fun `example part 2`() {
//        assertEquals(
//            0,
//            Day24().test(aocExample).value
//        )
//    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            0,
            Day24().solve(emptyList()).value
        )
    }

//    @Test
//    fun `solution part 2`() {
//        assertEquals(
//            0,
//            Day24().solve(emptyList()).value
//        )
//    }
}
