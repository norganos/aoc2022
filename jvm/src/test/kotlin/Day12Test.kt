import de.linkel.aoc.Day12
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day12Test {
    val aocExample = """
Sabqponm
abcryxxl
accszExk
acctuvwj
abdefghi
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            31,
            Day12().test(aocExample).steps
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            29,
            Day12().test(aocExample).shortestDecline
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            412,
            Day12().solve(emptyList()).steps
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            402,
            Day12().solve(emptyList()).shortestDecline
        )
    }
}
