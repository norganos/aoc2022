package de.linkel.aoc.utils.readers

import java.io.Reader
import kotlin.math.min

class PeekingReader(
    private val parent: Reader,
): Reader()  {
    private var buffer: CharArray? = null
    private var ended = false

    val buffered get(): Int = buffer?.size ?: 0

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        synchronized(lock) {
            val buf = buffer
            if (buf != null) {
                val consume = min(len, buf.size)
                buf.copyInto(cbuf, off, 0, consume)
                if (consume < buf.size) {
                    buffer = buf.copyOfRange(consume, buf.size)
                } else {
                    buffer = null
                }
                return consume
            }
            val read = parent.read(cbuf, off, len)
            if (read == -1) {
                ended = true
            }
            return read
        }
    }

    fun peek(cbuf: CharArray, off: Int, len: Int): Int {
        synchronized(lock) {
            val buf = buffer
            return if (buf != null) {
                if (buf.size >= len) {
                    buf.copyInto(cbuf, off, 0, len)
                    len
                } else {
                    val tmp = CharArray(len)
                    buf.copyInto(tmp, 0)
                    val read = parent.read(tmp, buf.size, len - buf.size)
                    if (read > 0) {
                        buffer = tmp.copyOfRange(0, read + buf.size)
                        tmp.copyInto(cbuf, off, 0, read + buf.size)
                        read + buf.size
                    } else {
                        if (read == -1) {
                            ended = true
                        }
                        tmp.copyInto(cbuf, off, 0, buf.size)
                        buf.size
                    }
                }
            } else if (ended) {
                return -1
            } else {
                val tmp = CharArray(len)
                val read = parent.read(tmp, 0, len)
                if (read == -1) {
                    ended = true
                } else {
                    tmp.copyInto(cbuf, off, 0, read)
                    buffer = tmp.copyOfRange(0, read)
                }
                read
            }
        }
    }

    fun peek(len: Int): String {
        val tmp = CharArray(len)
        val size = peek(tmp, 0, len)
        if (size == -1) {
            return ""
        }
        return String(tmp, 0, size)
    }

    override fun close() {
        buffer = null
        parent.close()
    }
}

fun Reader.peeking(): PeekingReader = PeekingReader(this)
