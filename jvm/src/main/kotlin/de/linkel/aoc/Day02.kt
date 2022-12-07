package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton


@Singleton
class Day02: AbstractLinesAdventDay<Day02.Result>() {
    override val day = 2

    val defeats = mapOf(
        Pick.ROCK to Pick.SCISSORS,
        Pick.SCISSORS to Pick.PAPER,
        Pick.PAPER to Pick.ROCK
    )
    val defeated = mapOf(
        Pick.ROCK to Pick.PAPER,
        Pick.SCISSORS to Pick.ROCK,
        Pick.PAPER to Pick.SCISSORS
    )

    val playerPicks = mapOf(
        "A" to PlayerPick(Player.OPPONENT, Pick.ROCK, "A"),
        "B" to PlayerPick(Player.OPPONENT, Pick.PAPER, "B"),
        "C" to PlayerPick(Player.OPPONENT, Pick.SCISSORS, "C"),
        "X" to PlayerPick(Player.MYSELF, Pick.ROCK, "X"),
        "Y" to PlayerPick(Player.MYSELF, Pick.PAPER, "Y"),
        "Z" to PlayerPick(Player.MYSELF, Pick.SCISSORS, "Z")
    )

    fun score(op: PlayerPick, mp: PlayerPick): Int {
        return mp.pick.score + if (defeats[mp.pick] == op.pick) {
            6
        } else if (defeats[op.pick] == mp.pick) {
            0
        } else {
            3
        }
    }

    fun needed(op: PlayerPick, outcome: String): PlayerPick? {
        return when(outcome) {
            "X" -> // lose
                PlayerPick(Player.MYSELF, defeats[op.pick]!!)
            "Y" -> // draw
                PlayerPick(Player.MYSELF, op.pick)
            "Z" -> // win
                PlayerPick(Player.MYSELF, defeated[op.pick]!!)
            else -> null
        }
    }

    override fun process(lines: Sequence<String>): Result {
        val games = lines
            .map { line ->
                val op = playerPicks[line.substring(0, 1)]!!
                val mp1 = playerPicks[line.substring(2, 3)]!!
                val mp2 = needed(op, line.substring(2, 3))!!
                Pair(score(op, mp1), score(op, mp2))
            }.toList()
        return Result(games.sumOf { it.first }, games.sumOf { it.second })
    }

    enum class Pick(val score: Int) {
        ROCK(1),
        PAPER(2),
        SCISSORS(3)
    }
    enum class Player {
        OPPONENT,
        MYSELF
    }

    data class PlayerPick(
        val who: Player,
        val pick: Pick,
        val code: String? = null
    )

    data class Result(
        val part1: Int,
        val part2: Int
    ) {
        override fun toString(): String {
            return "part1: $part1, part2: $part2"
        }
    }
}
