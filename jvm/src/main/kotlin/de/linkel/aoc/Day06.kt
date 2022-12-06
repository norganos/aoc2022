package de.linkel.aoc

import de.linkel.aoc.utils.Input
import de.linkel.aoc.utils.ReaderSequence
import jakarta.inject.Singleton
import java.io.Reader

@Singleton
class Day06: AdventDay {
    override val day = 6

    override fun solve(args: List<String>) {
        Input.from(args, "input06.txt").use { reader ->
            val buffer = CharBuffer(14)
            val found = reader.charSequence().indexOfFirst { char ->
                buffer.append(char)
                buffer.full && buffer.content.distinct().size == 14
            }

            println("start-of-message marker after ${found+1} characters")
        }
    }

    class CharBuffer(
        val size: Int,
        initial: Char = ' '
    ) {
        private val store: Array<Char> = generateSequence { initial }.take(size).toList().toTypedArray()
        private var next = 0
        private var length = 0

        val full get(): Boolean = length == size
        val content get(): List<Char> {
            val result = mutableListOf<Char>()
            repeat(length) { i ->
                result.add(store[(next + i) % size])
            }
            return result
        }

        fun append(c: Char) {
            store[next] = c
            next = (next + 1) % size
            if (length < size) {
                length++
            }
        }

        override fun toString(): String {
            return content.joinToString("")
        }
    }

    private fun Reader.charSequence(): ReaderSequence = ReaderSequence(this)
}
