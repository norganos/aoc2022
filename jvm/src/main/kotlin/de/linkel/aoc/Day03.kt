package de.linkel.aoc

import java.io.File


fun main(args: Array<String>) {
    File(args.firstOrNull() ?: "input03.txt").bufferedReader().use { reader ->
        reader.useLines { sequence ->
            val sum = sequence
                .flatMap { line ->
                    val middle = line.length / 2
                    line.substring(0, middle).toCharArray()
                        .filter { line.indexOf(it, middle) > -1 }
                        .distinct()
                }
                .map {
                    if (it.isLowerCase()) it - 'a' + 1 else it - 'A' + 27
                }
                .sum()
            println("sum: $sum")
        }
    }
    File(args.firstOrNull() ?: "input03.txt").bufferedReader().use { reader ->
        reader.useLines { sequence ->
            val sum = sequence
                .chunked(3)
                .flatMap { group ->
                    group.first().toCharArray().filter { c ->
                        group.takeLast(2).all { it.contains(c) }
                    }.distinct()
                }
                .map {
                    if (it.isLowerCase()) it - 'a' + 1 else it - 'A' + 27
                }
                .sum()
            println("badge sum: $sum")
        }
    }
}
