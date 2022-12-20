package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day20Test {
    val aocExample = """
1
2
-3
3
-2
0
4
          """.trimIndent()

    @Test
    fun `example part 1`() {
        val result = Day20(1, 1).test(aocExample)
        assertEquals(
            3L,
            result.value
        )
        assertEquals(
            listOf(-2L, 1L, 2L, -3L, 4L, 0L, 3L),
            result.numbers
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            1623178306L,
            Day20().test(aocExample).value
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            7713L,
            Day20(1, 1).solve(emptyList()).value
        )
    }

//    @Test
//    fun `solution part 2`() {
//        assertEquals(
//            2838,
//            Day20().solve(emptyList()).value
//        )
//    }
}

/*

Initial arrangement:
1, 2, -3, 3, -2, 0, 4                   0, 1, 2, 3, 4, 5, 6

                                        0 -> 0 => 0 + 1 % 7 = 1
1 moves between 2 and -3:               1, 2, 3, 4, 5, 6
2, 1, -3, 3, -2, 0, 4                   1, 0, 2, 3, 4, 5, 6

                                        1 -> 0 => 0 + 2 % 7 = 2
2 moves between -3 and 3:               0, 2, 3, 4, 5, 6
1, -3, 2, 3, -2, 0, 4                   0, 2, 1, 3, 4, 5, 6

                                        2 -> 1 => 1 - 3 % 7 = -2
-3 moves between -2 and 0:              0, 1, 3, 4, 5, 6
1, 2, 3, -2, -3, 0, 4                   0, 1, 3, 4, 2, 5, 6

                                        3 -> 2 => 2 + 3 % 7 = 5
3 moves between 0 and 4:                0, 1, 4, 2, 5, 6
1, 2, -2, -3, 0, 3, 4                   0, 1, 4, 2, 5, 3, 6

-2 moves between 4 and 1:
1, 2, -3, 0, 3, 4, -2

0 does not move:
1, 2, -3, 0, 3, 4, -2

4 moves between -3 and 0:
1, 2, -3, 4, 0, 3, -2
 */
