package de.linkel.aoc.utils.readers

import java.io.Reader

class EndAwareReader(
    private val parent: Reader,
): Reader()  {
    private var ended = false
    val hasEnded get() = ended

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        val r = parent.read(cbuf, off, len)
        if (r == -1) {
            ended = true
        }
        return r
    }

    override fun close() {
        ended = true
        parent.close()
    }
}

fun Reader.endAware(): EndAwareReader = EndAwareReader(this)
