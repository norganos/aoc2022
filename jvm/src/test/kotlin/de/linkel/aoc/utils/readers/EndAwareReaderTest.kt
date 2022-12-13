package de.linkel.aoc.utils.readers

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EndAwareReaderTest {
    @Test
    fun `fresh reader with data has not ended`() {
        val reader = "abc".reader().endAware()
        assertFalse(reader.hasEnded)
    }

    @Test
    fun `already used reader with data left has not ended`() {
        val reader = "abc".reader().endAware()
        reader.read()
        assertFalse(reader.hasEnded)
    }

    @Test
    fun `fully consumed reader with no data left has not ended yet`() {
        val reader = "abc".reader().endAware()
        reader.read(CharArray(5), 0, 5)
        assertFalse(reader.hasEnded)
    }

    @Test
    fun `fully consumed reader with no data left is marked as ended after tried read`() {
        val reader = "abc".reader().endAware()
        reader.read(CharArray(5), 0, 5)
        reader.read()
        assertTrue(reader.hasEnded)
    }

    @Test
    fun `fresh empty reader is marked as ended after tried read`() {
        val reader = "".reader().endAware()
        reader.read()
        assertTrue(reader.hasEnded)
    }
}
