package de.linkel.aoc.utils.readers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReaderSequenceTest {
    @Test
    fun `string with 4 characters streams out 4 characters`() {
        val ll = ReaderSequence("abcd".reader()).toList()
        assertEquals(4, ll.size)
        assertEquals('a', ll[0])
        assertEquals('b', ll[1])
        assertEquals('c', ll[2])
        assertEquals('d', ll[3])
    }

    @Test
    fun `string with 4 characters streams first characters correct`() {
        val sequence = ReaderSequence("abcd".reader())
        val iterator = sequence.iterator()
        assertTrue(iterator.hasNext())
        assertEquals('a', iterator.next())
    }

    @Test
    fun `string with 4 characters streams has no next after 4 chars`() {
        val sequence = ReaderSequence("abcd".reader())
        val iterator = sequence.iterator()
        iterator.next()
        iterator.next()
        iterator.next()
        iterator.next()
        assertFalse(iterator.hasNext())
    }

    @Test
    fun `string with 4 characters throws error after 4 nexts`() {
        val sequence = ReaderSequence("abcd".reader())
        val iterator = sequence.iterator()
        iterator.next()
        iterator.next()
        iterator.next()
        iterator.next()
        assertThrows<NoSuchElementException> { iterator.next() }
    }
}
