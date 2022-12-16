package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day16Test {
    val aocExample = """
Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
Valve BB has flow rate=13; tunnels lead to valves CC, AA
Valve CC has flow rate=2; tunnels lead to valves DD, BB
Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
Valve EE has flow rate=3; tunnels lead to valves FF, DD
Valve FF has flow rate=0; tunnels lead to valves EE, GG
Valve GG has flow rate=0; tunnels lead to valves FF, HH
Valve HH has flow rate=22; tunnel leads to valve GG
Valve II has flow rate=0; tunnels lead to valves AA, JJ
Valve JJ has flow rate=21; tunnel leads to valve II
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            1651,
            Day16().test(aocExample).sumReleasedPressure1
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            1707,
            Day16().test(aocExample).sumReleasedPressure2
        )
    }

//    @Test
//    fun `solution part 1`() {
//        assertEquals(
//            2253,
//            Day16().solve(emptyList()).sumReleasedPressure1
//        )
//    }

//    @Test
//    fun `solution part 2`() {
//        assertEquals(
//            2838,
//            Day16().solve(emptyList()).sumReleasedPressure2
//        )
//    }
}
