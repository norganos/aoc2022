package de.linkel.aoc.utils

import java.io.BufferedReader
import java.io.File

class Input {
    companion object {
        fun from(args: List<String>, name: String): BufferedReader {
            return if (args.isNotEmpty())
                    File(args.first()).bufferedReader()
                else
                    Input::class.java.getResourceAsStream("/$name")!!.bufferedReader()
        }
    }
}
