package utils.readers

import de.linkel.aoc.utils.readers.peeking
import de.linkel.aoc.utils.readers.scan
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ScanningReaderTest {
    @Test
    fun `without delimiters it reads the full content`() {
        val base = "hallo welt".reader().peeking()
        val reader = base.scan(emptyList())
        val buf = CharArray(10)
        assertEquals("hallo welt", String(buf, 0, reader.read(buf, 0, 10)))
        assertEquals(-1, base.read())
    }

    @Test
    fun `with delimiter it stops at the index`() {
        val base = "hallo welt".reader().peeking()
        val reader = base.scan(listOf(" "))
        val buf = CharArray(10)
        assertEquals("hallo", String(buf, 0, reader.read(buf, 0, 10)))
    }

    @Test
    fun `with delimiter it stops at the index and the underlying reader continues after the delimiter`() {
        val base = "hallo welt".reader().peeking()
        val reader = base.scan(listOf(" "))
        val buf = CharArray(10)
        assertEquals("hallo", String(buf, 0, reader.read(buf, 0, 10)))
        assertEquals("welt", String(buf, 0, base.read(buf, 0, 10)))
    }

    @Test
    fun `with delimiters it stops at the first found delimiter`() {
        val base = "hallo\twelt und\n so".reader().peeking()
        val reader = base.scan(listOf(" ", "\t", "\n", "---"))
        val buf = CharArray(10)
        assertEquals("hallo", String(buf, 0, reader.read(buf, 0, 10)))
    }
}
