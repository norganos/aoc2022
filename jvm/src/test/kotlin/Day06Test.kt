import de.linkel.aoc.Day06
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day06Test {
    val part1er = Day06(4)
    val part2er = Day06(14)
    
    @Test
    fun `part 1 mjqjpqmgbljsphdztnvjfqwrcgsmlb`() {
        assertEquals(
            7,
            part1er.test("mjqjpqmgbljsphdztnvjfqwrcgsmlb").length
        )
    }
    @Test
    fun `part 1 bvwbjplbgvbhsrlpgdmjqwftvncz`() {
        assertEquals(
            5,
            part1er.test("bvwbjplbgvbhsrlpgdmjqwftvncz").length
        )
    }
    @Test
    fun `part 1 nppdvjthqldpwncqszvftbrmjlhg`() {
        assertEquals(
            6,
            part1er.test("nppdvjthqldpwncqszvftbrmjlhg").length
        )
    }
    @Test
    fun `part 1 nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg`() {
        assertEquals(
            10,
            part1er.test("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg").length
        )
    }
    @Test
    fun `part 1 zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw`() {
        assertEquals(
            11,
            part1er.test("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw").length
        )
    }


    @Test
    fun `part 2 mjqjpqmgbljsphdztnvjfqwrcgsmlb`() {
        assertEquals(
            19,
            part2er.test("mjqjpqmgbljsphdztnvjfqwrcgsmlb").length
        )
    }

    @Test
    fun `part 2 bvwbjplbgvbhsrlpgdmjqwftvncz`() {
        assertEquals(
            23,
            part2er.test("bvwbjplbgvbhsrlpgdmjqwftvncz").length
        )
    }

    @Test
    fun `part 2 nppdvjthqldpwncqszvftbrmjlhg`() {
        assertEquals(
            23,
            part2er.test("nppdvjthqldpwncqszvftbrmjlhg").length
        )
    }

    @Test
    fun `part 2 nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg`() {
        assertEquals(
            29,
            part2er.test("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg").length
        )
    }

    @Test
    fun `part 2 zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw`() {
        assertEquals(
            26,
            part2er.test("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw").length
        )
    }
}
