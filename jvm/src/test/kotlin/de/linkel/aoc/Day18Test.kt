package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day18Test {
    val aocExample = """
2,2,2
1,2,2
3,2,2
2,1,2
2,3,2
2,2,1
2,2,3
2,2,4
2,2,6
1,2,5
3,2,5
2,1,5
2,3,5
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            64,
            Day18().test(aocExample).surface
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            58,
            Day18().test(aocExample).surface
        )
    }

//    @Test
//    fun `solution part 1`() {
//        assertEquals(
//            3173,
//            Day18().solve(emptyList()).surface
//        )
//    }

//    @Test
//    fun `solution part 2`() {
//        assertEquals(
//            2838,
//            Day18().solve(emptyList()).surface
//        )
//    }
}
