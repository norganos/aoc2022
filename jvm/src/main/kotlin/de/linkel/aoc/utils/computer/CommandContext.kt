package de.linkel.aoc.utils.computer

interface CommandContext {
    fun getEnv(name: String): String
}
