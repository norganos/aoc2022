package utils

import de.linkel.aoc.utils.TopList
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class TopListTest {
    @Test
    fun `top 3 list with 3 elements works`() {
        val top3 = TopList<Int>(3).plus(listOf(1, 2, 5, 3, 4)).toList()
        assertEquals(3, top3.size)
        assertContains(top3, 5)
        assertContains(top3, 4)
        assertContains(top3, 3)
        assertEquals(5, top3.first())
    }

    @Test
    fun `top 3 list with 1 element works`() {
        val top3 = TopList<Int>(3).plus(listOf(23)).toList()
        assertEquals(1, top3.size)
        assertContains(top3, 23)
        assertEquals(23, top3.first())
    }
}
