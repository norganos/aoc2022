package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day08bTest {
    val aocExample = """
30373
25512
65332
33549
35390
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            21,
            Day08b().test(aocExample).visible
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            8,
            Day08b().test(aocExample).highestScenicScore
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            1681,
            Day08b().solve(emptyList()).visible
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            201684,
            Day08b().solve(emptyList()).highestScenicScore
        )
    }
}
