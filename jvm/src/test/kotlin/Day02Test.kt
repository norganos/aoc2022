import de.linkel.aoc.Day02
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day02Test {
    val aocExample = """
A Y
B X
C Z
        """.trimIndent()

    @Test
    fun `part 1`() {
        assertEquals(
            15,
            Day02().test(aocExample).part1
        )
    }

    @Test
    fun `part 2`() {
        assertEquals(
            12,
            Day02().test(aocExample).part2
        )
    }
}
