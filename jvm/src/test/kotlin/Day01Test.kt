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
    fun `part 1`() {
        assertEquals(
            24000,
            Day01().test(aocExample).max
        )
    }

    @Test
    fun `part 2`() {
        assertEquals(
            45000,
            Day01().test(aocExample).top3
        )
    }
}
