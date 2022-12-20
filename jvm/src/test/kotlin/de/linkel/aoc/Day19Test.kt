package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day19Test {
    val aocExample = """
Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
          """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            33,
            Day19(24, -1).test(aocExample).qualityLevels
        )
    }

//    @Test
//    fun `example part 2`() {
//        assertEquals(
//            56*62,
//            Day19().test(aocExample).product
//        )
//    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            1681,
            Day19(24, -1).solve(emptyList()).qualityLevels
        )
    }

//    @Test
//    fun `solution part 2`() {
//        assertEquals(
//            5394,
//            Day19().solve(emptyList()).product
//        )
//    }
}
