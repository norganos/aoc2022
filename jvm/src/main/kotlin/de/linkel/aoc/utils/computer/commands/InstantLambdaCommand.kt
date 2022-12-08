package de.linkel.aoc.utils.computer.commands

import de.linkel.aoc.utils.computer.Command
import de.linkel.aoc.utils.computer.CommandContext

class InstantLambdaCommand(
    private val lambda: (args: List<String>) -> Unit,
    private val args: List<String>
): Command {
    override fun execute(commandContext: CommandContext) {
        lambda(args)
    }

    override fun append(line: String) {
    }

    override fun close() {
    }
}
