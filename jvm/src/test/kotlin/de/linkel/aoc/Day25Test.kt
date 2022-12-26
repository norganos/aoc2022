package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day25Test {
    val aocExample = """
1=-0-2
12111
2=0=
21
2=01
111
20012
112
1=-1=
1-12
12
1=
122
          """.trimIndent()

    @Test
    fun `snafu2int works`() {
        val d = Day25()
        assertEquals(1747, d.snafu2int("1=-0-2"))
        assertEquals(906, d.snafu2int("12111"))
        assertEquals(198, d.snafu2int("2=0="))
        assertEquals(11, d.snafu2int("21"))
        assertEquals(201, d.snafu2int("2=01"))
        assertEquals(31, d.snafu2int("111"))
        assertEquals(1257, d.snafu2int("20012"))
        assertEquals(32, d.snafu2int("112"))
        assertEquals(353, d.snafu2int("1=-1="))
        assertEquals(107, d.snafu2int("1-12"))
        assertEquals(7, d.snafu2int("12"))
        assertEquals(3, d.snafu2int("1="))
        assertEquals(37, d.snafu2int("122"))
    }

    @Test
    fun `int2snafu works`() {
        val d = Day25()
        assertEquals("1=-0-2", d.int2snafu(1747))
        assertEquals("12111", d.int2snafu(906))
        assertEquals("2=0=", d.int2snafu(198))
        assertEquals("21", d.int2snafu(11))
        assertEquals("2=01", d.int2snafu(201))
        assertEquals("111", d.int2snafu(31))
        assertEquals("20012", d.int2snafu(1257))
        assertEquals("112", d.int2snafu(32))
        assertEquals("1=-1=", d.int2snafu(353))
        assertEquals("1-12", d.int2snafu(107))
        assertEquals("12", d.int2snafu(7))
        assertEquals("1=", d.int2snafu(3))
        assertEquals("122", d.int2snafu(37))
    }

    @Test
    fun `example part 1`() {
        assertEquals(
            "2=-1=0",
            Day25().test(aocExample).sumSnafu
        )
    }

//    @Test
//    fun `example part 2`() {
//        assertEquals(
//            54,
//            Day25().test(aocExample).value
//        )
//    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            "2=10---0===-1--01-20",
            Day25().solve(emptyList()).sumSnafu
        )
    }

//    @Test
//    fun `solution part 2`() {
//        assertEquals(
//            717,
//            Day25().solve(emptyList()).value
//        )
//    }
}
