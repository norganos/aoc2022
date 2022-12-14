package de.linkel.aoc

import de.linkel.aoc.base.AbstractFileAdventDay
import de.linkel.aoc.utils.computer.Command
import de.linkel.aoc.utils.computer.CommandContext
import de.linkel.aoc.utils.computer.Terminal
import de.linkel.aoc.utils.computer.commandFactories.LambdaCommandFactory
import java.io.BufferedReader
import java.text.DecimalFormat

class Day07b: AbstractFileAdventDay<Day07b.Result>() {
    override val day = 7

    class FileSystemContext {
        val root = Dir(null, "")
        var cwd = root
    }

    class LsCommand(
        private val fs: FileSystemContext
    ): Command {
        override fun execute(commandContext: CommandContext) {
        }

        override fun append(line: String) {
            val space = line.indexOf(' ')
            val size = line.substring(0, space)
            val name = line.substring(space + 1)
            if (size == "dir") {
                fs.cwd.files.add(Dir(fs.cwd, name))
            } else {
                fs.cwd.files.add(File(fs.cwd, name, size.toLong()))
            }
        }

        override fun close() {
        }
    }
    class CdCommand(
        private val fs: FileSystemContext,
        private val args: List<String>
    ): Command {
        init {
            assert(args.size == 1)
        }

        override fun execute(commandContext: CommandContext) {
            when (val subdir = args[0]) {
                "/" -> {
                    fs.cwd = fs.root
                }
                ".." -> {
                    fs.cwd = fs.cwd.parent!!
                }
                else -> {
                    fs.cwd = fs.cwd.get(subdir)!! as Dir
                }
            }
        }

        override fun append(line: String) {
            val space = line.indexOf(' ')
            val size = line.substring(0, space)
            val name = line.substring(space + 1)
            if (size == "dir") {
                fs.cwd.files.add(Dir(fs.cwd, name))
            } else {
                fs.cwd.files.add(File(fs.cwd, name, size.toLong()))
            }
        }

        override fun close() {
        }
    }

    override fun process(reader: BufferedReader): Result {
        val fs = FileSystemContext()
        val commandFactory = LambdaCommandFactory(
            mapOf(
                "cd" to { args -> CdCommand(fs, args) },
                "ls" to { _ -> LsCommand(fs) },
            )
        )
        Terminal(
            commandFactory,
            prompt = "$ "
        ).process(reader)

        val smallDirs = findDirsSmaller(fs.root, 100000)
        println("${smallDirs.count()} small dirs in sum ${smallDirs.sumOf { it.size }}")

//        smallDirs.forEach { file ->
//            println("${file}  ${sizeFormat.format(file.size)}")
//        }

        val diskSize = 70000000
        val maxTotalSize = diskSize - 30000000
        if (fs.root.size < maxTotalSize) {
            return Result(smallDirs.count(), smallDirs.sumOf { it.size }, "", 0)
        }
        println("root is ${fs.root.size} needing ${fs.root.size - maxTotalSize} to reach $maxTotalSize")
        val deleteCandidate = findDirsBigger(fs.root, fs.root.size - maxTotalSize).minBy { it.size }
        println("deleting $deleteCandidate would free up ${deleteCandidate.size}, resulting in ${fs.root.size - deleteCandidate.size}")
        return Result(smallDirs.count(), smallDirs.sumOf { it.size }, deleteCandidate.toString(), deleteCandidate.size)
    }

    private val sizeFormat = DecimalFormat("#,##0")

    @Suppress("unused")
    private fun printout(dir: Dir, indent: Int = 0) {
        dir.files.sortedBy { it.name }.forEach { file ->
            if (file is Dir) {
                println("${" ".repeat(indent)} ${file.name}/  ${sizeFormat.format(file.size)}")
                printout(file, indent+2)
            } else {
                println("${" ".repeat(indent)} ${file.name}  ${sizeFormat.format(file.size)}")
            }
        }
    }

    private fun findDirsSmaller(dir: Dir, maxSize: Long): List<Dir> {
        val result = mutableListOf<Dir>()
        dir.files.forEach { file ->
            if (file is Dir) {
                if (file.size <= maxSize) {
                    result.add(file)
                }
                result.addAll(findDirsSmaller(file, maxSize))
            }
        }
        return result
    }

    private fun findDirsBigger(dir: Dir, minSize: Long): List<Dir> {
        val result = mutableListOf<Dir>()
        dir.files.forEach { file ->
            if (file is Dir) {
                if (file.size >= minSize) {
                    result.add(file)
                }
                result.addAll(findDirsBigger(file, minSize))
            }
        }
        return result
    }

    interface FileObject {
        val parent: Dir?
        val name: String
        val size: Long
    }

    class Dir(
        override val parent: Dir?,
        override val name: String
    ): FileObject {
        val files = mutableSetOf<FileObject>()
        override val size get(): Long = files.sumOf { it.size }
        fun get(name: String): FileObject? {
            return files.find { it.name == name }
        }

        override fun toString(): String {
            return if (parent != null) {
                "${parent}$name/"
            } else {
                "$name/"
            }
        }
    }

    class File(
        override val parent: Dir,
        override val name: String,
        override val size: Long
    ): FileObject {
        override fun toString(): String {
            return "${parent}$name"
        }
    }

    data class Result(
        val smallerCount: Int,
        val smallerSum: Long,
        val deletePath: String,
        val deleteSize: Long
    ) {
        override fun toString(): String {
            return "$smallerCount dirs smaller 100.000 in sum $smallerSum, deleting $deletePath frees up $deleteSize"
        }
    }
}
