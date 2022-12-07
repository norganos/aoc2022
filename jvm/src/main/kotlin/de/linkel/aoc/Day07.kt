package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton

@Singleton
class Day07: AbstractLinesAdventDay() {
    override val day = 7

    val root = Dir(null, "")
    var cwd = root
    var lastcmd = ""

    override fun process(lines: Sequence<String>) {
        val iterator = lines.iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            if (line.startsWith("$ cd ")) {
                val subdir = line.substring(5)
                if (subdir == "/") {
                    cwd = root
                    println("cd / => $cwd")
                } else if (subdir == "..") {
                    cwd = cwd.parent!!
                    println("cd .. => $cwd")
                } else {
                    cwd = cwd.get(subdir)!! as Dir
                    println("cd $subdir => $cwd")
                }
                lastcmd = line
            } else if (line == "$ ls") {
                lastcmd = line
                println("ls")
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

        val smallDirs = findDirsSmaller(root, 100000)
        println("${smallDirs.count()} small dirs in sum ${smallDirs.sumOf { it.size }}")
        val diskSize = 70000000
        val maxTotalSize = diskSize - 30000000

        println("root is ${root.size} needing ${root.size - maxTotalSize} to reach $maxTotalSize")
        val deleteCandidate = findDirsBigger(root, root.size - maxTotalSize).minBy { it.size }
        println("deleting $deleteCandidate would free up ${deleteCandidate.size}, resulting in ${root.size - deleteCandidate.size}")
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
}
