package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton
import java.util.regex.Pattern


@Singleton
class Day21(): AbstractLinesAdventDay<Day21.Result>() {
    override val day = 21

    val EQUALS = LambdaBinaryOp("a=b") { a, b -> if (a == b) 1 else 0 }
    val PLUS = LambdaBinaryOp("a+b") { a, b -> a + b }
    val A_MINUS_B = LambdaBinaryOp("a-b") { a, b -> a - b }
    val B_MINUS_A = LambdaBinaryOp("b-a") { a, b -> b - a }
    val POWER = LambdaBinaryOp("a*b") { a, b -> a * b }
    val A_DIV_B = LambdaBinaryOp("a/b") { a, b -> a / b }
    val B_DIV_A = LambdaBinaryOp("b/a") { a, b -> b / a }

    override fun process(lines: Sequence<String>): Result {
        val operations = mapOf(
            "+" to PLUS,
            "-" to A_MINUS_B,
            "*" to POWER,
            "/" to A_DIV_B
        )

        val lookup = mutableMapOf<String, Monkey>()
        val opPattern = Pattern.compile("(\\w+)\\s*([+\\-*/])\\s*(\\w+)")
        lines.forEach { line ->
            val name = line.substringBefore(": ")
            val shout = line.substringAfter(": ")
            val opMatch = opPattern.matcher(shout)
            if (opMatch.matches()) {
                lookup[name] = OpMonkey(
                    name,
                    lookup,
                    opMatch.group(1),
                    opMatch.group(3),
                    operations[opMatch.group(2)]!!
                )
            } else {
                lookup[name] = NumberMonkey(
                    name,
                    shout.toLong()
                )
            }
        }

        val part1 = lookup["root"]!!.number
        lookup.forEach { e ->
            e.value.let {
                if (it is OpMonkey) {
                    it.wipeMemory()
                }
            }
        }
        val root = lookup["root"].let { if (it is OpMonkey) it.copy(op = EQUALS) else throw IllegalStateException("root is no OpMonkey") }
        val human = Human()
        lookup["root"] = root
        lookup["humn"] = human
        assert(!root.ready) // we have to call ready on root to trigger all lookups and possible caching on all monkeys
        lookup.forEach { e ->
            e.value.let {
                if (it is OpMonkey) {
                    it.wipeMemory()
                }
            }
        }

        val chain = dfs(root)
        val part2 = chain.fold(0L) { a, op -> op.calc(a) }

        return Result(
            part1,
            part2
        )
    }

    private fun dfs(monkey: Monkey): List<UnaryOp> {
        if (monkey is Human) {
            return emptyList()
        }
        require(monkey is OpMonkey)
        monkey.findMonkeys()
        val ma = monkey.ma
        val mb = monkey.mb
        require(ma != null)
        require(mb != null)
        if (ma.ready == mb.ready) {
            throw IllegalStateException("expected only one downstream monkey to be not ready...")
        }
        return listOf(
            when (monkey.op.id) {
                "a=b" -> ConstantUnaryOp(if (ma.ready) ma.number else mb.number)
                "a+b" -> LambdaUnaryOp(if (ma.ready) ma.number else mb.number, A_MINUS_B)
                "a-b" -> if (ma.ready) LambdaUnaryOp(ma.number, B_MINUS_A) else LambdaUnaryOp(mb.number, PLUS)
                "a*b" -> LambdaUnaryOp(if (ma.ready) ma.number else mb.number, A_DIV_B)
                "a/b" -> if (ma.ready) LambdaUnaryOp(ma.number, B_DIV_A) else LambdaUnaryOp(mb.number, POWER)
                else -> throw IllegalStateException("unknown op ${monkey.op}")
            }
        ) + dfs(if (ma.ready) mb else ma)
    }

    interface Monkey {
        val name: String
        val ready: Boolean
        val number: Long
    }
    data class NumberMonkey(
        override val name: String,
        override val number: Long
    ): Monkey {
        override val ready = true
    }
    class Human: Monkey {
        override val name = "humn"
        override var number = 0L
        override var ready = false
    }
    data class OpMonkey(
        override val name: String,
        val lookup: Map<String, Monkey>,
        val a: String,
        val b: String,
        val op: BinaryOp
    ): Monkey {
        private var cachedReady = false
        var ma: Monkey? = null
        var mb: Monkey? = null

        fun wipeMemory() {
            ma = null
            mb = null
            cachedReady = false
        }
        fun findMonkeys() {
            if (ma == null) {
                ma = lookup[a]
            }
            if (mb == null) {
                mb = lookup[b]
            }
        }
        private fun checkReady(): Boolean {
            if (!cachedReady) {
                findMonkeys()
                if (ma!!.ready && mb!!.ready) {
                    cachedReady = true
                }
            }
            return cachedReady
        }
        override val ready get(): Boolean = checkReady()
        override val number get(): Long {
            if (!checkReady()) {
                throw IllegalStateException("Monkey not ready yet")
            }
            return op.calc(ma!!.number, mb!!.number)
        }
    }
    interface BinaryOp {
        val id: String
        fun calc(a: Long, b: Long): Long
    }
    class LambdaBinaryOp(
        override val id: String,
        val lambda: (a: Long, b: Long) -> Long
    ): BinaryOp {
        override fun calc(a: Long, b: Long): Long {
            return lambda(a, b)
        }

        override fun toString(): String {
            return id
        }
    }
    interface UnaryOp {
        fun calc(a: Long): Long
    }
    data class ConstantUnaryOp (
        val c: Long
    ): UnaryOp {
        override fun calc(a: Long): Long {
            return c
        }

        override fun toString(): String {
            return c.toString()
        }
    }
    class LambdaUnaryOp(
        val b: Long,
        val lambda: BinaryOp
    ): UnaryOp {
        override fun calc(a: Long): Long {
            return lambda.calc(a, b)
        }

        override fun toString(): String {
            return lambda.toString().replace("b", b.toString())
        }
    }

    data class Result(
        val value: Long,
        val humanValue: Long
    ) {
        override fun toString(): String {
            return "part1: $value, part2: $humanValue"
        }
    }
}
