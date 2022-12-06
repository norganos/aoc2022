package de.linkel.aoc.base

import java.io.BufferedReader

abstract class AbstractLinesAdventDay: AbstractFileAdventDay() {
    override fun process(reader: BufferedReader) {
        reader.useLines { sequence ->
            process(sequence)
        }
    }

    protected abstract fun process(lines: Sequence<String>)
}
