package de.linkel.aoc.base

import java.io.BufferedReader

abstract class AbstractLinesAdventDay<T>: AbstractFileAdventDay<T>() {
    override fun process(reader: BufferedReader): T {
        return reader.useLines { sequence ->
            process(sequence)
        }
    }

    protected abstract fun process(lines: Sequence<String>): T
}
