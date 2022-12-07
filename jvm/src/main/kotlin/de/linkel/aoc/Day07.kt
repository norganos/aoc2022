package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton
import java.text.DecimalFormat

@Singleton
class Day07: AbstractLinesAdventDay<Day07.Result>() {
    override val day = 7

    private final val root = Dir(null, "")
    var cwd = root
    var lastcmd = ""

    override fun process(lines: Sequence<String>): Result {
        val iterator = lines.iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            if (line.startsWith("$ cd ")) {
                val subdir = line.substring(5)
                if (subdir == "/") {
                    cwd = root
                } else if (subdir == "..") {
                    cwd = cwd.parent!!
                } else {
                    cwd = cwd.get(subdir)!! as Dir
                }
                lastcmd = line
            } else if (line == "$ ls") {
                lastcmd = line
            } else if (lastcmd == "$ ls") {
                val space = line.indexOf(' ')
                val size = line.substring(0, space)
                val name = line.substring(space + 1)
                if (size == "dir") {
                    cwd.files.add(Dir(cwd, name))
                } else {
                    cwd.files.add(File(cwd, name, size.toLong()))
                }
            } else {
                throw Exception("???")
            }
        }

//        printout(root)

        val smallDirs = findDirsSmaller(root, 100000)
        println("${smallDirs.count()} small dirs in sum ${smallDirs.sumOf { it.size }}")

//        smallDirs.forEach { file ->
//            println("${file}  ${sizeFormat.format(file.size)}")
//        }

        val diskSize = 70000000
        val maxTotalSize = diskSize - 30000000

        println("root is ${root.size} needing ${root.size - maxTotalSize} to reach $maxTotalSize")
        val deleteCandidate = findDirsBigger(root, root.size - maxTotalSize).minBy { it.size }
        println("deleting $deleteCandidate would free up ${deleteCandidate.size}, resulting in ${root.size - deleteCandidate.size}")
        return Result(smallDirs.count(), smallDirs.sumOf { it.size }, deleteCandidate.toString(), deleteCandidate.size)
    }

    private val sizeFormat = DecimalFormat("#,##0")

    @Suppress("unused")
    fun printout(dir: Dir, indent: Int = 0) {
        dir.files.sortedBy { it.name }.forEach { file ->
            if (file is Dir) {
                println("${" ".repeat(indent)} ${file.name}/  ${sizeFormat.format(file.size)}")
                printout(file, indent+2)
            } else {
                println("${" ".repeat(indent)} ${file.name}  ${sizeFormat.format(file.size)}")
            }
        }
    }

    fun findDirsSmaller(dir: Dir, maxSize: Long): List<Dir> {
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

    fun findDirsBigger(dir: Dir, minSize: Long): List<Dir> {
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
            if (parent != null) {
                return "${parent}$name/"
            } else {
                return "$name/"
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
