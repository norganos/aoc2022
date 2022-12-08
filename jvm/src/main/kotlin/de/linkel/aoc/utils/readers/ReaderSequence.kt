package de.linkel.aoc.utils.readers

import java.io.Reader
import java.util.NoSuchElementException

class ReaderSequence(
    private val reader: Reader,
    private val bufferSize: Int = 64
): Sequence<Char>, AutoCloseable, Iterator<Char> {
    override fun iterator(): Iterator<Char> = this

    override fun close() {
        reader.close()
    }

    private val buffer = charArrayOf(*" ".repeat(bufferSize).toCharArray())
    private var offset = 0
    private var size = 0

    override fun hasNext(): Boolean {
        if (offset < size) {
            return true
        }
        loadIfNecessary()
        return size > -1
    }

    private fun loadIfNecessary() {
        if (offset == size) {
            offset = 0
            size = reader.read(buffer, offset, bufferSize)
        }
    }

    override fun next(): Char {
        loadIfNecessary()
        if (offset < size) {
            val result = buffer[offset]
            offset++
            return result
        }
        throw NoSuchElementException()
    }
}
