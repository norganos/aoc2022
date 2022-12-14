package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.math.BigInteger

@Singleton
class Day11(
    @Suppress("MnInjectionPoints") @Value("2") val part: Int = 2,
    @Suppress("MnInjectionPoints") @Value("10000") val rounds: Int = 10000
): AbstractLinesAdventDay<Day11.Result>() {
    override val day = 11

    var relieveOp: Op = Identity()

    override fun process(lines: Sequence<String>): Result {
        val monkeys = parseMonkeys(lines)
        val sortedMonkeyIds = monkeys.keys.sorted()

        relieveOp = if (part == 2) Mod(monkeys.values.map { it.test.modFactor }.fold(1) { p, f -> p * f }) else Divide(3)

        monkeys.values.forEach { m ->
            m.items.replaceAll { item -> item.copy(relieveOp = relieveOp) }
        }

        repeat(rounds) {
            sortedMonkeyIds.forEach { id ->
                monkeys[id]!!.turn(monkeys)
            }
        }
        monkeys.values
            .sortedBy { it.id }
            .forEach { monkey ->
                println("Monkey ${monkey.id} inspected items ${monkey.inspections} times.")
            }

        val inspections = monkeys.values.sortedBy { it.id }.map { it.inspections }

        return Result(
            inspections
        )
    }

    private fun parseMonkeys(lines: Sequence<String>): Map<Int, Monkey> {
        val built = mutableListOf<Monkey?>()
        val monkeyBuilder = MonkeyBuilder(relieveOp)
        lines.forEach { line ->
            if (line.isBlank()) {
                built.add(monkeyBuilder.build())
            } else if (line.startsWith("Monkey ")) {
                monkeyBuilder.id = line.substring(7, line.length - 1).toInt()
            } else if (line.startsWith("  Starting items: ")) {
                monkeyBuilder.startItems.addAll(line.substring(18).split(',').map { it.trim().toLong() })
            } else if (line.startsWith("  Operation: new = old * old")) {
                monkeyBuilder.operation = Square()
            } else if (line.startsWith("  Operation: new = old * ")) {
                monkeyBuilder.operation = Multiply(line.substring(25).toInt())
            } else if (line.startsWith("  Operation: new = old + ")) {
                monkeyBuilder.operation = Plus(line.substring(25).toInt())
            } else if (line.startsWith("  Test: divisible by ")) {
                monkeyBuilder.testModFactor = line.substring(21).toInt()
            } else if (line.startsWith("    If true: throw to monkey ")) {
                monkeyBuilder.testTrueMonkey = line.substring(29).toInt()
            } else if (line.startsWith("    If false: throw to monkey ")) {
                monkeyBuilder.testFalseMonkey = line.substring(30).toInt()
            } else {
                throw Exception("don't know what to to with line '$line'")
            }
        }
        built.add(monkeyBuilder.build())
        return built.filterNotNull().associateBy { it.id }
    }

    class MonkeyBuilder(
        private val relieveOp: Op
    ) {
        companion object {
            private var itemCount = 0
        }

        var id = -1
        var startItems = mutableListOf<Long>()
        var operation: Op = Plus(0)
        var testModFactor = 1
        var testTrueMonkey = -1
        var testFalseMonkey = -1

        fun clear() {
            id = -1
            startItems.clear()
            operation = Plus(0)
            testModFactor = 1
            testTrueMonkey = -1
            testFalseMonkey = -1
        }

        fun build(): Monkey? {
            val result = if (id != -1 && testModFactor != 1 && testFalseMonkey != -1 && testTrueMonkey != -1)
                Monkey(
                    id = id,
                    initItems = startItems.map { Item(itemCount++, it.toBigInteger(), relieveOp) },
                    operation = operation,
                    test = Test(testModFactor, testTrueMonkey, testFalseMonkey)
                )
            else null
            clear()
            return result
        }
    }

    data class Item(
        val id: Int,
        val worryLevel: BigInteger,
        val relieveOp: Op
    ) {
        fun relieve(): Item = copy(
            worryLevel = relieveOp.calc(worryLevel)
        )
        fun performOp(op: Op) = copy(
            worryLevel = op.calc(worryLevel)
        )

        override fun toString(): String {
            return "Item #$id [$worryLevel]"
        }
    }

    interface Op {
        fun calc(input: BigInteger): BigInteger
    }
    class Identity: Op {
        override fun calc(input: BigInteger): BigInteger {
            return input
        }

        override fun toString(): String {
            return "old"
        }
    }
    class Multiply(val factor: Int): Op {
        override fun calc(input: BigInteger): BigInteger {
            return input.times(factor.toBigInteger())
        }

        override fun toString(): String {
            return "old * $factor"
        }
    }
    class Mod(val factor: Int): Op {
        override fun calc(input: BigInteger): BigInteger {
            return input.mod(factor.toBigInteger())
        }

        override fun toString(): String {
            return "old % $factor"
        }
    }
    class Divide(val factor: Int): Op {
        override fun calc(input: BigInteger): BigInteger {
            return input.divide(factor.toBigInteger())
        }

        override fun toString(): String {
            return "old * $factor"
        }
    }
    class Square: Op {
        override fun calc(input: BigInteger): BigInteger {
            return input * input
        }

        override fun toString(): String {
            return "old * old"
        }
    }
    class Plus(val amount: Int): Op {
        override fun calc(input: BigInteger): BigInteger {
            return input + amount.toBigInteger()
        }

        override fun toString(): String {
            return "old + $amount"
        }
    }
    data class Test(
        val modFactor: Int,
        val trueResult: Int,
        val falseResult: Int
    )

    class Monkey(
        val id: Int,
        initItems: List<Item>,
        val operation: Op,
        val test: Test
    ) {
        val items = mutableListOf(*initItems.toTypedArray())
        var inspections = 0L

        fun turn(monkeys: Map<Int, Monkey>) {
            val iterator = items.iterator()
            while (iterator.hasNext()) {
                inspections++
                val item0 = iterator.next()
                val item1 = item0.performOp(operation)
                val item = item1.relieve()
                iterator.remove()
                val dest = if (item.worryLevel.mod(test.modFactor.toBigInteger()) == BigInteger.ZERO) test.trueResult else test.falseResult
//                println("    ${item0.worryLevel} -> ${item1.worryLevel} % ${test.modFactor} == ${item1.worryLevel % test.modFactor}  =>  $dest")
                monkeys[dest]!!.items.add(item)
            }
        }

        override fun toString(): String {
            return "Monkey $id: ${items.joinToString(", ")}"
        }
    }

    data class Result(
        val inspections: List<Long>
    ) {
        val inspectionsProduct: Long = inspections.sortedDescending()
            .take(2)
            .fold(1) { p, m -> p * m}
        override fun toString(): String {
            return "product of two most inspections: $inspectionsProduct"
        }
    }
}
