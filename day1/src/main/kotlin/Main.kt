import java.io.File

fun main(args: Array<String>) {
    File(args.firstOrNull() ?: "input.txt").bufferedReader().use { reader ->
        reader.useLines { sequence ->
            val sorted = sequence
                .fold(Pair(0, emptyList<Int>())) { state, line ->
                    if (line.isEmpty()) {
                        state.copy(
                            first = 0,
                            second = state.second + listOf(state.first)
                        )
                    } else {
                        state.copy(
                            first = state.first + line.toInt()
                        )
                    }
                }
                .let { state ->
                    state.second + listOf(state.first)
                }
                .sortedDescending()
            val max = sorted.first()
            val top3 = sorted.take(3).sum()
            assert(max == 72511)
            println("max: $max")
            println("top3: $top3")
        }
    }
}
