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
            Day09(1).test(aocExample).tailTrailLength
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            1,
            Day09().test(aocExample).tailTrailLength
        )
    }

    val aocExample2 = """
R 5
U 8
L 8
D 3
R 17
D 10
L 25
U 20
    """.trimIndent()

    @Test
    fun `bigger example part 2`() {
        assertEquals(
            36,
            Day09().test(aocExample2).tailTrailLength
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            6337,
            Day09(1).solve(emptyList()).tailTrailLength
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            2455,
            Day09().solve(emptyList()).tailTrailLength
        )
    }
}
