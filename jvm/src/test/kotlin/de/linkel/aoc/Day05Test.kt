package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day05Test {
    val aocExample = """\
    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2
"""

    @Test
    fun `loading works`() {
        val d = Day05()
        val stacks = d.loadStacks(aocExample.reader().buffered(1024))
        assertEquals(3, stacks.size)
        assertEquals('N', stacks["1"]!!.top)
        assertEquals("NZ", stacks["1"]!!.stack)
        assertEquals('D', stacks["2"]!!.top)
        assertEquals("DCM", stacks["2"]!!.stack)
        assertEquals('P', stacks["3"]!!.top)
        assertEquals("P", stacks["3"]!!.stack)
    }

    @Test
    fun `example part 1`() {
        val d = Day05(Day05.Crane.CrateMover9000)
        assertEquals(
            "CMZ",
            d.test(aocExample).tops
        )
    }

    @Test
    fun `example part 2`() {
        val d = Day05(Day05.Crane.CrateMover9001)
        assertEquals(
            "MCD",
            d.test(aocExample).tops
        )
    }

    @Test
    fun `solution part 1`() {
        val d = Day05(Day05.Crane.CrateMover9000)
        assertEquals(
            "CVCWCRTVQ",
            d.solve(emptyList()).tops
        )
    }

    @Test
    fun `solution part 2`() {
        val d = Day05(Day05.Crane.CrateMover9001)
        assertEquals(
            "CNSCZWLVT",
            d.solve(emptyList()).tops
        )
    }
}
