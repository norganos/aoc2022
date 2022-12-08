package de.linkel.aoc.utils.computer

interface CommandLineParser {
    fun parse(readable: Readable): CommandLine
}
