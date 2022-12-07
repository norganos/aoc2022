package de.linkel.aoc

import de.linkel.aoc.base.AbstractFileAdventDay
import de.linkel.aoc.utils.ReaderSequence
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.io.BufferedReader
import java.io.Reader

@Singleton
class Day06(
    @Suppress("MnInjectionPoints") @Value("14") val minLength: Int = 14
): AbstractFileAdventDay<Day06.Result>() {

    override val day = 6

    override fun process(reader: BufferedReader): Result {
        val buffer = CharBuffer(minLength)
        val found = reader.charSequence().indexOfFirst { char ->
            buffer.append(char)
            buffer.full && buffer.content.distinct().size == minLength
        }

        return Result(found+1)
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

    data class Result(
        val length: Int
    ) {
        override fun toString(): String {
            return "marker after $length chars"
        }
    }
}
