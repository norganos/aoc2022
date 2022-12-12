package de.linkel.aoc.utils.computer

import java.lang.IllegalStateException
import java.nio.CharBuffer

class PosixCommandLineParser(
    @Suppress("unused") val commandContext: CommandContext = DefaultCommandContext()
): CommandLineParser {
    fun replaceVariables(token: String): String {
        //TODO: env vars ersetzen
        return token
    }

    override fun parse(readable: Readable): CommandLine {
        val result = mutableListOf<String>()
        val currentToken: StringBuilder = StringBuilder()

        var state = WHITESPACE

        val cb = CharBuffer.allocate(1)
        while (readable.read(cb) > 0) {
            val c = cb[0]
            cb.clear()
            when(state) {
                WHITESPACE -> {
                    if (c == '\r') {
                        // ignore
                    } else if (c == '\n') {
                        break
                    } else if (c.isWhitespace() || c.isISOControl())  {
                        continue
                    } else if (c == '\'') {
                        state = SINGLE_QUOTES
                    } else if (c == '"') {
                        state = DOUBLE_QUOTES
                    } else if (c == '\\') {
                        state = EscapedState(TEXT) // implicitly switch to TEXT state when a backslash occurs in whitespace state
                    } else { // if we get here
                        state = TEXT
                        currentToken.append(c)
                    }
                }
                TEXT -> {
                    if (c == '\n') {
                        break
                    } else if (c.isWhitespace() || c.isISOControl())  {
                        result.add(replaceVariables(currentToken.toString()))
                        currentToken.clear()
                        state = WHITESPACE
                    } else if (c == '\\') {
                        state = EscapedState(state)
                    } else {
                        currentToken.append(c)
                    }
                }
                SINGLE_QUOTES -> {
                    if (c == '\\') {
                        state = EscapedState(state)
                    } else if (c == '\'') {
                        result.add(currentToken.toString()) // no env substitution
                        currentToken.clear()
                        state = EXPECT_WHITESPACE
                    } else {
                        currentToken.append(c)
                    }
                }
                DOUBLE_QUOTES -> {
                    if (c == '\\') {
                        state = EscapedState(state)
                    } else if (c == '"') {
                        result.add(replaceVariables(currentToken.toString()))
                        currentToken.clear()
                        state = EXPECT_WHITESPACE
                    } else {
                        currentToken.append(c)
                    }
                }
                EXPECT_WHITESPACE -> {
                    if (c == '\n') {
                        break
                    } else if (c.isWhitespace())  {
                        state = WHITESPACE
                    } else if (c.isISOControl()) {
                        // ignore
                    } else {
                        throw IllegalStateException("expected whitespace, got '$c'")
                    }
                }
                is EscapedState -> {
                    //TODO: escape sequences
                    currentToken.append(c)
                    state = state.parentState
                }
            }
        }
        if (state == TEXT) {
            result.add(currentToken.toString())
        }

        return CommandLine(result.first(), result.drop(1).toList())
    }

    open class State(val name: String) {
        override fun toString(): String {
            return name
        }
    }
    val EXPECT_WHITESPACE = State("expect whitespace")
    val WHITESPACE = State("whitespace")
    val TEXT = State("text")
    val SINGLE_QUOTES = State("single quotes")
    val DOUBLE_QUOTES = State("double quotes")
    data class EscapedState(val parentState: State): State("escape in ${parentState.name}")
}
