import de.linkel.aoc.Day04
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day04Test {
    val aocExample = """
2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            2,
            Day04().test(aocExample).contained
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            4,
            Day04().test(aocExample).overlapping
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            556,
            Day04().solve(emptyList()).contained
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            876,
            Day04().solve(emptyList()).overlapping
        )
    }
}
