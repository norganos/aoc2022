package de.linkel.aoc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day07bTest {
    val aocExample = """
${'$'} cd /
${'$'} ls
dir a
14848514 b.txt
8504156 c.dat
dir d
${'$'} cd a
${'$'} ls
dir e
29116 f
2557 g
62596 h.lst
${'$'} cd e
${'$'} ls
584 i
${'$'} cd ..
${'$'} cd ..
${'$'} cd d
${'$'} ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k
    """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            95437,
            Day07b().test(aocExample).smallerSum
        )
        assertEquals(
            2,
            Day07b().test(aocExample).smallerCount
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            24933642,
            Day07b().test(aocExample).deleteSize
        )
        assertEquals(
            "/d/",
            Day07b().test(aocExample).deletePath
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            1517599,
            Day07b().solve(emptyList()).smallerSum
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            2481982,
            Day07b().solve(emptyList()).deleteSize
        )
    }
}
