package de.linkel.aoc.base

import de.linkel.aoc.AdventDay
import java.io.BufferedReader
import java.io.File
import java.text.DecimalFormat
import kotlin.math.min

abstract class AbstractFileAdventDay<T>: AdventDay<T> {
    private val msFormat = DecimalFormat("#,##0")

    companion object {
        fun from(args: List<String>, name: String): BufferedReader {
            return if (args.isNotEmpty())
                File(args.first()).bufferedReader()
            else
                AbstractFileAdventDay::class.java.getResourceAsStream("/$name")!!.bufferedReader()
        }
    }

    override fun solve(args: List<String>): T {
        return from(args, String.format("input%02d.txt", day)).use { reader ->
            callProcess(reader)
        }
    }

    fun test(input: String): T {
        return input.reader().buffered(min(input.length, 1024)).use { reader ->
            callProcess(reader)
        }
    }

    private fun callProcess(reader: BufferedReader): T {
        println("solving AoC 2022 Day $day")
        val start = System.currentTimeMillis()
        val solution = process(reader)
        val duration = System.currentTimeMillis() - start
        println("Solution is $solution")
        println ("calculation took ${msFormat.format(duration)}ms")
        return solution
    }

    protected abstract fun process(reader: BufferedReader): T
}
