package de.linkel.aoc.utils.computer.commandFactories

import de.linkel.aoc.utils.computer.Command
import de.linkel.aoc.utils.computer.CommandFactory
import de.linkel.aoc.utils.computer.CommandLine

class LambdaCommandFactory(
    private val commands: Map<String, (args: List<String>) -> Command>
): CommandFactory {
    override fun create(commandLine: CommandLine): Command? {
        return commands[commandLine.command]?.let { it(commandLine.arguments) }
    }
}
