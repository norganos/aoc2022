package de.linkel.aoc.utils

import java.io.Reader

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
        return reader.ready()
    }

    override fun next(): Char {
        if (offset < size) {
            val result = buffer[offset]
            offset++
            return result
        }
        offset = 0
        size = reader.read(buffer, offset, bufferSize)
        return next()
    }
}
