package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import jakarta.inject.Singleton
import java.lang.IllegalStateException
import java.lang.StringBuilder
import java.util.*
import java.util.regex.Pattern

typealias ValvePath = List<Day16.Valve>

@Singleton
class Day16(): AbstractLinesAdventDay<Day16.Result>() {
    override val day = 16

    override fun process(lines: Sequence<String>): Result {
        var startValve = "AA"
        val pattern = Pattern.compile("Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)$")
        val valves = lines
            .filter { it.isNotBlank() }
            .map { line ->
                val match = pattern.matcher(line)
                if (match.matches()) {
                    if (startValve.isEmpty()) {
                        startValve = match.group(1)
                    }
                    ValveTemplate(
                        match.group(1),
                        match.group(2).toInt(),
                        match.group(3).split(",").map { it.trim() }
                    )
                } else {
                    throw IllegalArgumentException("can't parse line '$line'")
                }
            }
            .associateBy { it.id }
            .let { templateMap ->
                val valveMap = templateMap.mapValues {
                    Valve(it.value.id, it.value.flowRate)
                }
                templateMap.forEach { e ->
                    valveMap[e.key]!!.next.addAll(
                        e.value.next.map { valveMap[it]!! }
                    )
                }
                valveMap.forEach { e ->
                    e.value.paths = calculateDijkstraDistances(e.value)
                }
                valveMap
            }

        val bestSolutionHolder = SolutionHolder()
        val initValveVisitLog = ValveVisitLog(
            30,
            valves.values.count { it.flowRate > 0 }
        )
        dfs(valves[startValve]!!, bestSolutionHolder, initValveVisitLog)
        assert(factorial(initValveVisitLog.valveCount.toLong()) == bestSolutionHolder.visitedPaths)

        return Result(
            bestSolutionHolder.score,
            bestSolutionHolder.visitedPaths,
        )
    }

    private fun factorial(num: Long): Long {
        var factorial: Long = 1
        for (i in 1..num) {
            factorial *= i
        }
        return factorial
    }

    private fun dfs(
        current: Valve,
        bestSolutionHolder: SolutionHolder,
        log: ValveVisitLog
    ) {
        if (log.depth >= log.maxDepth || log.openValves.size == log.valveCount) {
            bestSolutionHolder.add(log)
//            val releases = log.visits
//                .take(log.depth)
//                .map { it.openingFlowRate ?: 0 }
//                .runningFold(0) { p, e ->
//                    p + e
//                }
//                .toList()
//                .let { r ->
//                    if (r.size < log.maxDepth) {
//                        r + List(log.maxDepth - r.size) { _ -> r.last() }
//                    } else {
//                        r
//                    }
//                }
//                .take(log.maxDepth)
//            assert(log.score == releases.sum())
            return
        }
        if (!log.isOpen(current) && current.flowRate > 0) {
            log.open(current)
            dfs(current, bestSolutionHolder, log)
            log.leave()
        } else {
            val maxSize = 30 - log.depth
            val nexts = current.paths
                .filter { !log.isOpen(it.last()) && it.size <= maxSize && it.last().flowRate > 0 }.toList()
            assert(nexts.size <= log.valveCount - log.openValves.size)
            for (next in nexts) {
                log.visit(next)
                dfs(next.last(), bestSolutionHolder, log)
                log.leave(next.size)
            }
            if (nexts.isEmpty()) {
                val anyValve = current.next.first()
                log.visit(anyValve)
                dfs(anyValve, bestSolutionHolder, log)
                log.leave()
            }
        }
    }

    class ValveVisitLog(
        val maxDepth: Int,
        val valveCount: Int
    ) {
        val visits = List(maxDepth) { _ -> ValveVisitLogEntry() }.toTypedArray()
        var depth = 0
        var score = 0
        val openValves: MutableSet<String> = mutableSetOf()

        fun visit(valve: Valve) {
            visits[depth].valve = valve.id
            visits[depth].openingFlowRate = null
            depth++
        }
        fun visit(valves: ValvePath) {
            valves.forEach { valve ->
                visits[depth].valve = valve.id
                visits[depth].openingFlowRate = null
                depth++
            }
        }
        fun open(valve: Valve) {
            val current = visits[depth-1]
            if (current.valve in openValves) {
                throw IllegalStateException("${current.valve} already open")
            }
            if (current.valve != valve.id) {
                throw IllegalStateException("${valve.id} is not current value (${current.valve})")
            }
            visits[depth].valve = current.valve
            visits[depth].openingFlowRate = valve.flowRate
            depth++
            score += (maxDepth - depth) * valve.flowRate
            openValves.add(current.valve)
        }
        fun leave(count: Int = 1) {
            repeat(count) {
                val last = visits[depth-1]
                if (last.openingFlowRate != null) {
                    openValves.remove(last.valve)
                    score -= (maxDepth - depth) * last.openingFlowRate!!
                }
                last.valve = "  "
                last.openingFlowRate = null
                depth--
            }
        }
        fun isOpen(valve: Valve): Boolean {
            return valve.id in openValves
        }

        override fun toString(): String {
            val sb = StringBuilder()
            visits.forEach { v ->
                if (v.openingFlowRate != null) {
                    sb.append("__")
                } else {
                    sb.append(v.valve)
                }
                sb.append(" ")
            }
            sb.append("= ")
            sb.append(score.toString())
            return sb.toString()
        }
    }

    class ValveVisitLogEntry {
        var valve: String = "  "
        var openingFlowRate: Int? = null
        override fun toString(): String {
            if (openingFlowRate != null) {
                return "release $openingFlowRate from $valve"
            } else {
                return "go to $valve"
            }
        }
    }
    class SolutionHolder {
        var solution: List<ValveVisitLogEntry>? = null
        var score: Int = 0
        var visitedPaths: Long = 0

        fun add(s: ValveVisitLog) {
            visitedPaths++
            assert(s.depth <= s.maxDepth)
            if (s.score > this.score) {
                this.score = s.score
                this.solution = listOf(*s.visits).subList(0, s.depth)
            }
        }
    }

    data class ValveTemplate(
        val id: String,
        val flowRate: Int,
        val next: List<String>
    )
    data class Valve(
        val id: String,
        val flowRate: Int
    ) {
        val next = mutableSetOf<Valve>()
        var paths: List<List<Valve>> = emptyList()
    }


    private fun calculateDijkstraDistances(start: Valve): List<ValvePath> {
        val visited = mutableMapOf<String, DijkstraNode>()
        val queue = PriorityQueue<DijkstraNode>()
        queue.add(DijkstraNode(start, 0, emptyList()))
        while (queue.isNotEmpty()) {
            val node = queue.poll()
            visited[node.valve.id] = node
            node.valve.next.forEach { nextValve ->
                if (nextValve.id !in visited) {
                    queue.add(
                        DijkstraNode(nextValve, node.distance + 1, node.intermediates + listOf(node.valve))
                    )
                }
            }
        }
        return visited.values
            .filter { it.valve !== start }
            .sortedBy { it.distance }
            .map {
                listOf(*it.intermediates.drop(1).toTypedArray(), it.valve)
            }
            .toList()
    }

    private data class DijkstraNode(
        val valve: Valve,
        val distance: Int,
        val intermediates: List<Valve>
    ): Comparable<DijkstraNode> {
        override fun compareTo(other: DijkstraNode): Int {
            return distance.compareTo(other.distance)
        }
    }

    data class Result(
        val sumReleasedPressure: Int,
        val visitedPaths: Long
    ) {
        override fun toString(): String {
            return "can release $sumReleasedPressure pressure at most (tried $visitedPaths different paths)"
        }
    }
}
