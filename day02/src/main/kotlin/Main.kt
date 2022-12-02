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
    val code: String
)

val defeats = mapOf(
    Pick.ROCK to Pick.SCISSORS,
    Pick.SCISSORS to Pick.PAPER,
    Pick.PAPER to Pick.ROCK
)

val playerPicks = listOf(
    PlayerPick(Player.OPPONENT, Pick.ROCK, "A"),
    PlayerPick(Player.OPPONENT, Pick.PAPER, "B"),
    PlayerPick(Player.OPPONENT, Pick.SCISSORS, "C"),
    PlayerPick(Player.MYSELF, Pick.ROCK, "X"),
    PlayerPick(Player.MYSELF, Pick.PAPER, "Y"),
    PlayerPick(Player.MYSELF, Pick.SCISSORS, "Z")
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

fun main(args: Array<String>) {
    File(args.firstOrNull() ?: "input.txt").bufferedReader().use { reader ->
        reader.useLines { sequence ->
            val sum = sequence
                .map { line ->
                    val op = playerPicks.find { it.code == line.substring(0, 1) }!!
                    val mp = playerPicks.find { it.code == line.substring(2, 3) }!!
                    score(op, mp)
                }
                .sum()
            println("sum: ${sum}")
        }
    }
}
