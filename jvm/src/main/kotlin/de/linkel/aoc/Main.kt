package de.linkel.aoc

import io.micronaut.configuration.picocli.PicocliRunner
import jakarta.inject.Inject
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.util.*

@Command(name = "aoc", description = ["..."])
class Main : Runnable {
    @Inject
    lateinit var days: List<AdventDay>

    @Parameters(index = "0", defaultValue = "0")
    var day: Int = 0

    @Parameters(index = "1..*")
    var args: List<String> = emptyList()

    override fun run() {
        if (day == 0) {
            val today = Calendar.getInstance()
            if (today.get(Calendar.MONTH) == Calendar.DECEMBER) {
                day = today.get(Calendar.DAY_OF_MONTH)
            }
        }
        val adventDay = days.find{ it.day == day }
        println("solving AoC 2022 Day ${adventDay!!.day}")
        adventDay.solve(args)
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            PicocliRunner.run(Main::class.java, *args)
        }
    }
}
