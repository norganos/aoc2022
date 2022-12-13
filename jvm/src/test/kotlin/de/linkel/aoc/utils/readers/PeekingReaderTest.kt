package de.linkel.aoc.utils.readers

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PeekingReaderTest {
    @Test
    fun `read returns correct content`() {
        val reader = "hallo welt".reader().peeking()
        val buf = CharArray(10)
        assertEquals("hallo welt", String(buf, 0, reader.read(buf, 0, 10)))
    }

    @Test
    fun `data peeked can still be read`() {
        val reader = "hallo welt".reader().peeking()
        assertEquals("hallo welt", reader.peek(10))
        val buf = CharArray(10)
        assertEquals("hallo welt", String(buf, 0, reader.read(buf, 0, 10)))
    }

    @Test
    fun `data can be peeked multiple times`() {
        val reader = "hallo welt".reader().peeking()
        assertEquals("hallo welt", reader.peek(10))
        assertEquals("hallo welt", reader.peek(10))
        val buf = CharArray(10)
        assertEquals("hallo welt", String(buf, 0, reader.read(buf, 0, 10)))
    }

    @Test
    fun `peeked is reset after read`() {
        val reader = "hallo welt".reader().peeking()
        assertEquals("hallo", reader.peek(5))
        val buf = CharArray(5)
        assertEquals("hallo", String(buf, 0, reader.read(buf, 0, 5)))
        assertEquals(" welt", reader.peek(5))
    }

    @Test
    fun `multiple consecutive peeks can extend the buffer`() {
        val reader = "hallo welt".reader().peeking()
        assertEquals("hallo", reader.peek(5))
        assertEquals("hallo welt", reader.peek(10))
        assertEquals("hallo", reader.peek(5))
        val buf = CharArray(10)
        assertEquals("hallo welt", String(buf, 0, reader.read(buf, 0, 10)))
    }

    @Test
    fun `peek on empty buffer returns empty`() {
        val reader = "hallo welt".reader().peeking()
        val buf = CharArray(10)
        assertEquals("hallo welt", String(buf, 0, reader.read(buf, 0, 10)))
        assertEquals("", reader.peek(5))
    }

    @Test
    fun `peek on ended buffer returns empty`() {
        val reader = "hallo welt".reader().peeking()
        val buf = CharArray(10)
        assertEquals("hallo welt", String(buf, 0, reader.read(buf, 0, 10)))
        reader.read()
        assertEquals("", reader.peek(5))
    }

    @Test
    fun `peek on fresh empty buffer returns empty`() {
        val reader = "".reader().peeking()
        assertEquals("", reader.peek(5))
    }
}
