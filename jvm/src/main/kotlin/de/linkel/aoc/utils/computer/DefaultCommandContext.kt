package de.linkel.aoc.utils.computer

open class DefaultCommandContext: CommandContext {
    private val envs = mutableMapOf<String,String>()
    override fun getEnv(name: String): String {
        return envs[name] ?: ""
    }
    fun setEnv(name: String, value: String) {
        envs[name] = value
    }
}
