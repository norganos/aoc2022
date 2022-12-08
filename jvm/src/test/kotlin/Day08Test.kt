import de.linkel.aoc.Day08
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day08Test {
    val aocExample = """
30373
25512
65332
33549
35390
        """.trimIndent()

    @Test
    fun `part 1`() {
        assertEquals(
            21,
            Day08().test(aocExample).visible
        )
    }

    @Test
    fun `part 2`() {
    }
}
