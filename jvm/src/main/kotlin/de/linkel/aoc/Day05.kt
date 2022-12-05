package de.linkel.aoc

import de.linkel.aoc.utils.Input
import jakarta.inject.Singleton
import java.io.BufferedReader

@Singleton
class Day05: AdventDay {
    override val day = 5

    private fun loadStacks(reader: BufferedReader): Map<String, Stack> {
        val stacks = mutableListOf<TmpStack>()
        while (true) {
            val line = reader.readLine()
            if (line.trim().isEmpty())
                break
            val count = (line.length + 1) / 4
            while (stacks.size < count) {
                stacks.add(TmpStack())
            }
            stacks.forEachIndexed { i, p ->
                val token = line.substring(4 * i, 4 * i + 2)
                if (token != "  ") {
                    if (token[0] == '[') {
                        p.stack.deserialize(token[1])
                    } else if (token[0] == ' ') {
                        p.name = "${token[1]}"
                    }
                }
            }
        }
        return stacks.associate { it.name to it.stack }
    }

    override fun solve(args: List<String>) {
        Input.from(args, "input05.txt").use { reader ->
            val stacks = loadStacks(reader)
            reader.useLines { sequence ->
                sequence.forEach { line ->
                    val tokens = line.trim().split(" ")
                    assert(tokens.size == 6)
                    assert(tokens[0] == "move")
                    assert(tokens[2] == "from")
                    assert(tokens[4] == "to")
                    stacks[tokens[3]]!!.move(to = stacks[tokens[5]]!!, count = tokens[1].toInt())
                }
                val tops = stacks.keys.sorted().map { stacks[it]!!.top  }.joinToString("")
                println("tops: $tops")
            }
        }
    }


    class Stack {
        private val content = mutableListOf<Char>()

        val top get(): Char = content.first()

        fun deserialize(c: Char) {
            content.add(c)
        }

        fun move(to: Stack, count: Int = 1) {
            // for CrateMover 9000
//            repeat(count) {
//                val box = content.removeAt(0)
//                to.put(box)
//            }
            // for CrateMover 9001
            val tmp = mutableListOf<Char>()
            repeat(count) {
                tmp.add(content.removeAt(0))
            }
            to.put(tmp)
        }

        fun put(box: Char) {
            content.add(0, box)
        }
        fun put(boxes: List<Char>) {
            boxes.reversed().forEach { box ->
                content.add(0, box)
            }
        }
    }

    class TmpStack(
        var name: String = "?",
        val stack: Stack = Stack()
    )
}
