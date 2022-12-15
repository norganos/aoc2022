package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day15Test {
    val aocExample = """
Sensor at x=2, y=18: closest beacon is at x=-2, y=15
Sensor at x=9, y=16: closest beacon is at x=10, y=16
Sensor at x=13, y=2: closest beacon is at x=15, y=3
Sensor at x=12, y=14: closest beacon is at x=10, y=16
Sensor at x=10, y=20: closest beacon is at x=10, y=16
Sensor at x=14, y=17: closest beacon is at x=10, y=16
Sensor at x=8, y=7: closest beacon is at x=2, y=10
Sensor at x=2, y=0: closest beacon is at x=2, y=10
Sensor at x=0, y=11: closest beacon is at x=2, y=10
Sensor at x=20, y=14: closest beacon is at x=25, y=17
Sensor at x=17, y=20: closest beacon is at x=21, y=22
Sensor at x=16, y=7: closest beacon is at x=15, y=3
Sensor at x=14, y=3: closest beacon is at x=15, y=3
Sensor at x=20, y=1: closest beacon is at x=15, y=3
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            26,
            Day15(10).test(aocExample).value
        )
    }

//    @Test
//    fun `example part 2`() {
//        assertEquals(
//            0,
//            Day15(10).test(aocExample).value
//        )
//    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            5525847,
            Day15().solve(emptyList()).value
        )
    }

//    @Test
//    fun `solution part 2`() {
//        assertEquals(
//            0,
//            Day15().solve(emptyList()).value
//        )
//    }
}
