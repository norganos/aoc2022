package de.linkel.aoc.utils.computer

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PosixCommandLineParserTest {
    private val parser = PosixCommandLineParser()

    private fun parse(input: String): CommandLine {
        return input.reader().use { reader ->
            parser.parse(reader)
        }
    }

    @Test
    fun `can parse ls`() {
        assertEquals(
            CommandLine("ls"),
            parse("ls")
        )
    }
    @Test
    fun `can parse simple cd upwards`() {
        assertEquals(
            CommandLine("cd", listOf("..")),
            parse("cd ..")
        )
    }
    @Test
    fun `can parse simple rm -rf dir with wildcard`() {
        assertEquals(
            CommandLine("rm", listOf("-rf", "/tmp/*")),
            parse("rm -rf /tmp/*")
        )
    }
    @Test
    fun `whitespaces at the beginning are ignores`() {
        assertEquals(
            CommandLine("cd", listOf("/tmp/")),
            parse("   cd /tmp/")
        )
    }
    @Test
    fun `whitespaces at the end are ignores`() {
        assertEquals(
            CommandLine("cd", listOf("/tmp/")),
            parse("cd /tmp/   ")
        )
    }
    @Test
    fun `whitespaces in the middle are ignored`() {
        assertEquals(
            CommandLine("cd", listOf("/tmp/")),
            parse("cd   /tmp/")
        )
    }
    @Test
    fun `multiple types of whitespaces in the middle are ignored`() {
        assertEquals(
            CommandLine("cd", listOf("/tmp/")),
            parse("cd\t  \t /tmp/")
        )
    }
    @Test
    fun `double quotes work with whitespaces and single quotes inside`() {
        assertEquals(
            CommandLine("echo", listOf("hallo 'welt'")),
            parse("echo \"hallo 'welt'\"")
        )
    }
    @Test
    fun `single quotes work with whitespaces and double quotes inside`() {
        assertEquals(
            CommandLine("echo", listOf("hallo \"welt\"")),
            parse("echo 'hallo \"welt\"'")
        )
    }
}
