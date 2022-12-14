package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.lang.IllegalArgumentException

@Singleton
class Day10(
    @Suppress("MnInjectionPoints") @Value("1") val length: Int = 9
): AbstractLinesAdventDay<Day10.Result>() {
    override val day = 10

    override fun process(lines: Sequence<String>): Result {
        val pc = Computer(lines.iterator()) { instr ->
            if (instr.startsWith("addx ")) {
                AddX(instr.substring(5).toInt())
            } else if (instr == "noop") {
                Noop()
            } else {
                throw IllegalArgumentException("unknown instruction '$instr'")
            }
        }
        val x40 = mutableListOf<Int>()
        while (!pc.tick()) {
//            println("${if ((pc.cycles - 20) % 40 == 0) "-" else " "} ${pc.cycles}: ${pc.currentCommand.toString().padEnd(9)} ${pc.registers.x}")
            if ((pc.cycles - 20) % 40 == 0) {
//                println("${pc.cycles}: ${pc.currentCommand.toString().padEnd(9)} ${pc.registers.x}")
                x40.add(pc.registers.x * pc.cycles)
            }
        }
        return Result(x40.sum(), pc.screen.toString())
    }

    class Screen {
        private val output = StringBuilder()
        private var pos = 1
        fun tick(computer: Computer) {

            if (pos >= computer.registers.x  && pos <= computer.registers.x + 2) {
                output.append("#")
            } else {
                output.append(".")
            }
            pos++
            if (pos == 41) {
                output.append("\n")
                pos = 1
            }
        }
        fun clear() {
            output.clear()
        }
        fun stop() {
            output.append("\n")
        }
        override fun toString(): String {
            return output.toString()
        }
    }

    interface Instruction {
        fun tick(registers: Registers): Boolean
    }

    class Computer(
        private val instructions: Iterator<String>,
        val instructionFactory: (instr: String) -> Instruction,
    ) {
        private val stop = Noop()
        val screen = Screen()
        private var currentCommand: Instruction = Noop()
            private set
        var cycles = 0
            private set
        val registers = Registers()

        fun tick(): Boolean {
            cycles++
            val done = currentCommand.tick(registers)
            if (done) {
                currentCommand = if (instructions.hasNext()) {
                    instructionFactory(instructions.next())
                } else {
                    stop
                }
            }
            if (currentCommand === stop) {
                screen.stop()
            } else if (cycles > 0) {
                screen.tick(this)
            } else if (cycles == 0) {
                screen.clear()
            }
            return done && currentCommand === stop
        }
    }

    class Registers {
        var x = 1
    }

    class Noop: Instruction {
        override fun tick(registers: Registers): Boolean {
            return true
        }

        override fun toString(): String {
            return "noop"
        }
    }

    class AddX(val amount: Int): Instruction {
        private var cycles = 0
        override fun tick(registers: Registers): Boolean {
            cycles++
            if (cycles == 2) {
                registers.x += amount
                return true
            }
            return false
        }

        override fun toString(): String {
            return "addx $amount"
        }
    }

    data class Result(
        val sum40: Int,
        val screen: String
    ) {
        override fun toString(): String {
            return "Sum of Signal Strengts every 40 cycles: $sum40\n$screen"
        }
    }
}
