package de.linkel.aoc.utils.computer

import de.linkel.aoc.utils.readers.endAware
import de.linkel.aoc.utils.readers.peeking
import de.linkel.aoc.utils.readers.scan
import java.io.Reader
import java.lang.IllegalStateException
import java.util.*

class Terminal(
    val commandFactory: CommandFactory,
    val commandContext: CommandContext = DefaultCommandContext(),
    val commandLineParser: CommandLineParser = PosixCommandLineParser(commandContext),
    val prompt: String = "$ "
) {
    private val lock = Object()

    fun process(inputReader: Reader) {
        val endAware = inputReader.endAware()
        endAware.peeking().use { reader ->
            synchronized(lock) {
                var activeCommand: Command? = null
                if (prompt.isNotEmpty()) {
                    val beginning = reader.peek(prompt.length + 4)
                    val firstPrompt = beginning.indexOf(prompt)
                    if (firstPrompt > -1 && beginning.substring(0, firstPrompt).isBlank()) {
                        reader.read(CharArray(firstPrompt + prompt.length), 0, firstPrompt + prompt.length)
                    }
                }
                while (!endAware.hasEnded || reader.buffered > 0) {
                    val currentCommand = activeCommand
                    if (currentCommand == null) {
                        val cmdline = commandLineParser.parse(reader)
                        if (cmdline.command == "exit") {
                            break
                        }
                        val newCommand = commandFactory.create(cmdline)
                        if (newCommand == null) {
                            throw IllegalStateException("unknown command '${cmdline.command}'")
                        }
                        activeCommand = newCommand
                        newCommand.execute(commandContext)
                        // peek if the next token is a prompt right away and if so, close the command
                        if (prompt.isNotEmpty() && reader.peek(prompt.length) == prompt) {
                            reader.read(CharArray(prompt.length), 0, prompt.length)
                            newCommand.close()
                            activeCommand = null
                        }
                    } else {
                        Scanner(reader.scan(listOf("\u0004", "\n$prompt"))).use { scanner ->
                            while (scanner.hasNextLine()) {
                                currentCommand.append(scanner.nextLine())
                            }
                        }
                        currentCommand.close()
                        activeCommand = null
                    }
                }
                activeCommand?.close()
            }
        }
    }
}
