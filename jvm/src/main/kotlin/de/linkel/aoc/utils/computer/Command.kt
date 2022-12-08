package de.linkel.aoc.utils.computer

interface Command {
    fun execute(commandContext: CommandContext)
    fun append(line: String)
    fun close()
}
