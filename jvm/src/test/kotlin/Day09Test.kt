import de.linkel.aoc.Day09
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day09Test {
    val aocExample = """
R 4
U 4
L 3
D 1
R 4
D 1
L 5
R 2
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            13,
            Day09().test(aocExample).tailTrailLength
        )
    }

    @Test
    fun `example part 2`() {
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            6337,
            Day09().solve(emptyList()).tailTrailLength
        )
    }

    @Test
    fun `solution part 2`() {
    }
}
