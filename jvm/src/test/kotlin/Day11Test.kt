import de.linkel.aoc.Day11
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day11Test {
    val aocExample = """
Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            10605L,
            Day11(1, 20).test(aocExample).inspectionsProduct
        )
    }

    @Test
    fun `example part 2 Round 1`() {
        assertEquals(
            listOf(2L, 4L, 3L, 6L),
            Day11(2, 1).test(aocExample).inspections
        )
    }

    @Test
    fun `example part 2 Round 20`() {
        assertEquals(
            listOf(99L, 97L, 8L, 103L),
            Day11(2, 20).test(aocExample).inspections
        )
    }

    @Test
    fun `example part 2 Round 1000`() {
        assertEquals(
            listOf(5204L, 4792L, 199L, 5192L),
            Day11(2, 1000).test(aocExample).inspections
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            2713310158L,
            Day11().test(aocExample).inspectionsProduct
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            58056L,
            Day11(1, 20).solve(emptyList()).inspectionsProduct
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            7,
            Day11().solve(emptyList()).inspectionsProduct
        )
    }
}
