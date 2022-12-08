import de.linkel.aoc.Day01
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day01Test {
    val aocExample = """
1000
2000
3000

4000

5000
6000

7000
8000
9000

10000
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            24000,
            Day01().test(aocExample).max
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            45000,
            Day01().test(aocExample).top3
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            72511,
            Day01().solve(emptyList()).max
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            212117,
            Day01().solve(emptyList()).top3
        )
    }
}
