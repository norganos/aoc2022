package de.linkel.aoc.utils.computer.commandFactories

import de.linkel.aoc.utils.computer.Command
import de.linkel.aoc.utils.computer.CommandFactory
import de.linkel.aoc.utils.computer.CommandLine

class DelegatingCommandFactory(
    private val factories: List<CommandFactory>
): CommandFactory {
    override fun create(commandLine: CommandLine): Command? {
        return factories.firstNotNullOfOrNull { it.create(commandLine) }
    }
}
