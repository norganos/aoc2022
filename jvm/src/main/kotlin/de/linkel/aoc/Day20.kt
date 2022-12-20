package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton

@Singleton
class Day20(
    @Suppress("MnInjectionPoints") @Value("811589153") val decryptionKey: Int = 811_589_153,
    @Suppress("MnInjectionPoints") @Value("10") val rounds: Int = 10,

): AbstractLinesAdventDay<Day20.Result>() {
    override val day = 20

    override fun process(lines: Sequence<String>): Result {
        val numbers = lines
            .map { it.toLong() * decryptionKey }
            .toMutableList()
        val size = numbers.size

        // wir arbeiten auf einem array aus den indices im alten array, damit
        val indices = (0 until size)
            .toMutableList()
        repeat(rounds) {
            for (i in (0 until size)) {
                val oldIndex = indices.indexOf(i)
                indices.removeAt(oldIndex)
                val newIndex =
                    Math.floorMod(numbers[i] + oldIndex, size - 1) // size - 1 weil die liste ja jetz kuerzer is
                indices.add(newIndex, i)
            }
        }

        val zero = indices.indexOf(numbers.indexOf(0))
        val sum = listOf(1000, 2000, 3000).sumOf { i ->
            numbers[indices[Math.floorMod((zero + i), size)]]
        }
        return Result(
            indices.map { numbers[it] }.toList(),
            sum
        )

        // des war der Holzweg:
        // erste Idee war auf dem Original-Array zu arbeiten, die Nummern zu verschieben, und sich eine Skip-List von Indices zu merken,
        // die übersprungen werden müssen, weil eine Zahl dorthin verschoben wurde.
        // diese Skip-List muss natürlich umgerechnet werden, wenn eine weitere Zahl weiter nach hinten geschoben wird (weil sich der
        // Index der vorderen Zahlen ja verringert)...
        // long story short: die Idee fand ich zwar cool (weniger memory footprint als endgültige Lösung), aber bei der echten Aufgabe
        // komm ich einfach nicht auf die richtige Lösung (Vermutung, dass beim mehrfachen Wrap was schieflauft, weil die Zahlen so gross sind?)
//        val skipOffsets = mutableSetOf<Int>()
//        var i = 0
//        while (i < size) {
//            if (i in skipOffsets) {
//                i++
//                skipOffsets.remove(i)
//                continue
//            }
//            val num = numbers[i]
//            numbers.removeAt(i)
//            val dest = Math.floorMod(i + num, size - 1)
//            numbers.add(dest, num)
//            if (dest <= i) {
//                i++
//            } else {
//                skipOffsets.add(dest)
//                for (j in (i+1) until dest) {
//                    if (j in skipOffsets) {
//                        skipOffsets.remove(j)
//                        skipOffsets.add(j-1)
//                    }
//                }
//            }
//        }
//
//        val zero = numbers.indexOf(0)
//        val sum = numbers[Math.floorMod(zero + 1000, size)] + numbers[Math.floorMod(zero + 2000, size)] + numbers[Math.floorMod(zero + 3000, size)]
//
//        return Result(
//            numbers,
//            sum
//        )
    }

    data class Result(
        val numbers: List<Long>,
        val value: Long
    ) {
        override fun toString(): String {
            return "$value"
        }
    }
}
