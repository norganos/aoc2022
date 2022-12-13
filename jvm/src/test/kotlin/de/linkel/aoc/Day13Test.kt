package de.linkel.aoc

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@MicronautTest
class Day13Test {
    @Inject
    lateinit var day: Day13

    val aocExample = """
[1,1,3,1,1]
[1,1,5,1,1]

[[1],[2,3,4]]
[[1],4]

[9]
[[8,7,6]]

[[4,4],4,4]
[[4,4],4,4,4]

[7,7,7,7]
[7,7,7]

[]
[3]

[[[]]]
[[]]

[1,[2,[3,[4,[5,6,7]]]],8,9]
[1,[2,[3,[4,[5,6,0]]]],8,9]
        """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            13,
            day.test(aocExample).sum
        )
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            140,
            day.test(aocExample).key
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            5760,
            day.solve(emptyList()).sum
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            26670,
            day.solve(emptyList()).key
        )
    }
}
