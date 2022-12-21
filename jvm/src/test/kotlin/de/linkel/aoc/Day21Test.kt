package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day21Test {
    val aocExample = """
root: pppw + sjmn
dbpl: 5
cczh: sllz + lgvd
zczc: 2
ptdq: humn - dvpt
dvpt: 3
lfqf: 4
humn: 5
ljgn: 2
sjmn: drzm * dbpl
sllz: 4
pppw: cczh / lfqf
lgvd: ljgn * ptdq
drzm: hmdt - zczc
hmdt: 32
          """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            152,
            Day21().test(aocExample).value
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            301,
            Day21().test(aocExample).humanValue
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            78342931359552L,
            Day21().solve(emptyList()).value
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            3296135418820L,
            Day21().solve(emptyList()).humanValue
        )
    }
}
