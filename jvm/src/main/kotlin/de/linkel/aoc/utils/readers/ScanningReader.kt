package de.linkel.aoc.utils.readers

import java.io.Reader
import kotlin.math.min

class ScanningReader(
    private val peekingReader: PeekingReader,
    private val endPatterns: List<String>,
    private val bufferSize: Int = 128
): Reader()  {
    private var ended = false
    private val buffer = CharArray((endPatterns.maxOfOrNull { it.length } ?: 0) + bufferSize)
    private var bufferOffset = 0
    private var bufferFilled = 0

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        synchronized(lock) {
            if (ended && bufferOffset == bufferFilled) {
                return -1
            }
            var written = 0
            fillBuffer()
            while (written < len && bufferOffset < bufferFilled) {
                val copyChars = min(len - written, bufferFilled - bufferOffset)
                buffer.copyInto(cbuf, off + written, bufferOffset, copyChars)
                bufferOffset += copyChars
                written += copyChars
                fillBuffer()
            }
            return written
        }
    }

    private fun fillBuffer() {
        if (ended) {
            return
        }
        if (bufferOffset >= bufferSize / 2) {
            buffer.copyInto(buffer, 0, bufferOffset, bufferFilled)
            bufferFilled -= bufferOffset
            bufferOffset = 0
        }
        val peeked = peekingReader.peek(buffer, bufferFilled, buffer.size - bufferFilled)
        if (peeked == -1) {
            ended = true
        } else if (peeked > 0) {
            val preview = String(buffer, 0, bufferFilled + peeked)
            val endPattern = endPatterns
                .map {
                    Pair(it, preview.indexOf(it))
                }
                .filter { it.second > -1 }
                .minByOrNull { it.second }
            if (endPattern != null) {
                ended = true
                val read = peekingReader.read(buffer, bufferFilled, endPattern.second + endPattern.first.length - bufferFilled)
                assert(read == endPattern.second + endPattern.first.length - bufferFilled)
                bufferFilled += endPattern.second // we drop the found endPattern
            } else {
                val read = peekingReader.read(buffer, bufferFilled, peeked)
                if (read == -1) {
                    ended = true
                } else {
                    bufferFilled += read
                }
            }
        }
    }

    override fun close() {
        // we must not close the underlying reader
    }
}

fun PeekingReader.scan(endPatterns: List<String>): ScanningReader = ScanningReader(this, endPatterns)
