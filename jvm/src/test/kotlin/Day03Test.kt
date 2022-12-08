import de.linkel.aoc.Day03
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day03Test {
    val aocExample = """
vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            157,
            Day03().test(aocExample).double
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            70,
            Day03().test(aocExample).badge
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            7889,
            Day03().solve(emptyList()).double
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            2825,
            Day03().solve(emptyList()).badge
        )
    }
}
