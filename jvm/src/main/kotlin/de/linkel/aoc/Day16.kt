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

        val log1 = ValveVisitLog(
            1,
            30,
            valves.values.count { it.flowRate > 0 }
        )
        val start1 = System.currentTimeMillis()
        dfs(arrayOf(valves["AA"]!!), log1)
        println("Part 1 took ${(System.currentTimeMillis() - start1)}ms")
        val log2 = ValveVisitLog(
            2,
            26,
            valves.values.count { it.flowRate > 0 }
        )
        val start2 = System.currentTimeMillis()
        dfs(arrayOf(valves["AA"]!!, valves["AA"]!!), log2)
        println("Part 2 took ${(System.currentTimeMillis() - start2)}ms")

        return Result(
            log1.bestScore,
            log2.bestScore,
        )
    }

    private fun dfs(
        current: Array<Valve>,
        log: ValveVisitLog
    ) {
        if (log.done) {
            log.commit()
            return
        }
        val worker = log.idleWorker
        val maxSize = log.maxDepth - log.depths[worker]
        val nexts = current[worker].paths
            .filter { !log.isOpen(it.last()) && it.size + 1 <= maxSize && it.last().flowRate > 0 }.toList()
        assert(nexts.size <= log.valveCount - log.openValves.size)
        for (next in nexts) {
            log.visit(worker, next)
            val nextPos = current.copyOf()
            nextPos[worker] = next.last()
            log.open(worker, nextPos[worker])
            dfs(nextPos, log)
            log.leave(worker, next.size+1)
        }
        if (nexts.isEmpty()) {
            val anyValve = current[worker].next.first()
            log.visit(worker, anyValve)
            val nextPos = current.copyOf()
            nextPos[worker] = anyValve
            dfs(nextPos, log)
            log.leave(worker)
        }
    }


    class ValveVisitLog(
        val workers: Int,
        val maxDepth: Int,
        val valveCount: Int
    ) {
        val visits = List(workers) { _ ->
            List(maxDepth) { _ -> ValveVisitLogEntry() }.toTypedArray()
        }.toTypedArray()
        val depths = List(workers) { _ -> 0 }.toTypedArray()
        var score = 0
        val openValves: MutableSet<String> = mutableSetOf()

        var bestSolution: List<Array<ValveVisitLogEntry>>? = null
        var bestScore = 0

        val done get(): Boolean {
            return openValves.size == valveCount ||
                (0 until workers)
                .all { depths[it] >= maxDepth }
        }

        val idleWorker get(): Int {
            return (0 until workers)
                .minBy { depths[it] }
        }

        fun visit(worker: Int, valve: Valve) {
            visits[worker][depths[worker]].valve = valve.id
            visits[worker][depths[worker]].openingFlowRate = null
            depths[worker]++
        }
        fun visit(worker: Int, valves: ValvePath) {
            valves.forEach { valve ->
                visits[worker][depths[worker]].valve = valve.id
                visits[worker][depths[worker]].openingFlowRate = null
                depths[worker]++
            }
        }
        fun open(worker: Int, valve: Valve) {
            val current = visits[worker][depths[worker]-1]
            if (current.valve in openValves) {
                throw IllegalStateException("${current.valve} already open")
            }
            if (current.valve != valve.id) {
                throw IllegalStateException("${valve.id} is not current value (${current.valve})")
            }
            visits[worker][depths[worker]].valve = current.valve
            visits[worker][depths[worker]].openingFlowRate = valve.flowRate
            depths[worker]++
            score += (maxDepth - depths[worker]) * valve.flowRate
            openValves.add(current.valve)
        }
        fun leave(worker: Int, count: Int = 1) {
            repeat(count) {
                val last = visits[worker][depths[worker]-1]
                if (last.openingFlowRate != null) {
                    openValves.remove(last.valve)
                    score -= (maxDepth - depths[worker]) * last.openingFlowRate!!
                }
                last.valve = "  "
                last.openingFlowRate = null
                depths[worker]--
            }
        }
        fun isOpen(valve: Valve): Boolean {
            return valve.id in openValves
        }

        override fun toString(): String {
            val sb = StringBuilder()
            (0 until maxDepth).forEach { i ->
                (0 until workers).forEach { w ->
                    if (w > 0) {
                        sb.append("/")
                    }
                    if (visits[w][i].openingFlowRate != null) {
                        sb.append("__")
                    } else {
                        sb.append(visits[w][i].valve)
                    }
                }
                sb.append(" ")
            }
            sb.append("= ")
            sb.append(score.toString())
            return sb.toString()
        }

        fun commit() {
            if ((0 until workers).any { depths[it] > maxDepth }) {
                println("ignore solution ${toString()} because of depthts: ${depths.joinToString("/")}")
            } else {
                if (score > bestScore) {
                    bestScore = score
                    bestSolution = (0 until workers)
                        .map { w ->
                            visits[w].copyOfRange(0, depths[w])
                        }
                }
            }
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
        val sumReleasedPressure1: Int,
        val sumReleasedPressure2: Int
    ) {
        override fun toString(): String {
            return "maximum pressure release alone: $sumReleasedPressure1, with an elephant: $sumReleasedPressure2"
        }
    }
}
