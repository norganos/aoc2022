package de.linkel.aoc.base

import de.linkel.aoc.AdventDay
import java.io.BufferedReader
import java.io.File

abstract class AbstractFileAdventDay: AdventDay {

    companion object {
        fun from(args: List<String>, name: String): BufferedReader {
            return if (args.isNotEmpty())
                File(args.first()).bufferedReader()
            else
                AbstractFileAdventDay::class.java.getResourceAsStream("/$name")!!.bufferedReader()
        }
    }

    override fun solve(args: List<String>) {
        from(args, String.format("input%02d.txt", day)).use { reader ->
            process(reader)
        }
    }

    protected abstract fun process(reader: BufferedReader)
}
