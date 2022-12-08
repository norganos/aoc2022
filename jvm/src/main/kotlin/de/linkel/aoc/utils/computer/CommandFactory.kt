package de.linkel.aoc.utils.computer

interface CommandFactory {
    fun create(commandLine: CommandLine): Command?
}
