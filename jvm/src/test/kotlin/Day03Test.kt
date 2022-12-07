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
    fun `part 1`() {
        assertEquals(
            157,
            Day03().test(aocExample).double
        )
    }

    @Test
    fun `part 2`() {
        assertEquals(
            70,
            Day03().test(aocExample).badge
        )
    }
}
