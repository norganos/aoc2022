package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day06Test {
    private val part1er = Day06(4)
    private val part2er = Day06(14)
    
    @Test
    fun `example part 1 mjqjpqmgbljsphdztnvjfqwrcgsmlb`() {
        assertEquals(
            7,
            part1er.test("mjqjpqmgbljsphdztnvjfqwrcgsmlb").length
        )
    }
    @Test
    fun `example part 1 bvwbjplbgvbhsrlpgdmjqwftvncz`() {
        assertEquals(
            5,
            part1er.test("bvwbjplbgvbhsrlpgdmjqwftvncz").length
        )
    }
    @Test
    fun `example part 1 nppdvjthqldpwncqszvftbrmjlhg`() {
        assertEquals(
            6,
            part1er.test("nppdvjthqldpwncqszvftbrmjlhg").length
        )
    }
    @Test
    fun `example part 1 nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg`() {
        assertEquals(
            10,
            part1er.test("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg").length
        )
    }
    @Test
    fun `example part 1 zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw`() {
        assertEquals(
            11,
            part1er.test("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw").length
        )
    }


    @Test
    fun `example part 2 mjqjpqmgbljsphdztnvjfqwrcgsmlb`() {
        assertEquals(
            19,
            part2er.test("mjqjpqmgbljsphdztnvjfqwrcgsmlb").length
        )
    }

    @Test
    fun `example part 2 bvwbjplbgvbhsrlpgdmjqwftvncz`() {
        assertEquals(
            23,
            part2er.test("bvwbjplbgvbhsrlpgdmjqwftvncz").length
        )
    }

    @Test
    fun `example part 2 nppdvjthqldpwncqszvftbrmjlhg`() {
        assertEquals(
            23,
            part2er.test("nppdvjthqldpwncqszvftbrmjlhg").length
        )
    }

    @Test
    fun `example part 2 nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg`() {
        assertEquals(
            29,
            part2er.test("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg").length
        )
    }

    @Test
    fun `example part 2 zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw`() {
        assertEquals(
            26,
            part2er.test("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw").length
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            1658,
            part1er.solve(emptyList()).length
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            2260,
            part2er.solve(emptyList()).length
        )
    }
}
