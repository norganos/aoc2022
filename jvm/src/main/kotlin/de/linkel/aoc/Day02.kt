import java.io.File

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
    "C" to PlayerPick(Player.OPPONENT, Pick.SCISSORS, "C")
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

fun main(args: Array<String>) {
    File(args.firstOrNull() ?: "input02.txt").bufferedReader().use { reader ->
        reader.useLines { sequence ->
            val sum = sequence
                .map { line ->
                    val op = playerPicks[line.substring(0, 1)]!!
                    val mp = needed(op, line.substring(2, 3))!!
                    score(op, mp)
                }
                .sum()
            println("sum: ${sum}")
        }
    }
}
