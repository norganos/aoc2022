package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.space.discrete.Point3d
import de.linkel.aoc.utils.space.discrete.Segment
import de.linkel.aoc.utils.space.discrete.Vector3d
import jakarta.inject.Singleton
import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
@Singleton
class Day18(): AbstractLinesAdventDay<Day18.Result>() {
    override val day = 18

    companion object {
        val RIGHT = Vector3d(1, 0, 0)
        val LEFT = Vector3d(-1, 0, 0)
        val BACK = Vector3d(0, 1, 0)
        val FORTH = Vector3d(0, -1, 0)
        val UP = Vector3d(0, 0, 1)
        val DOWN = Vector3d(0, 0, -1)
    }

    override fun process(lines: Sequence<String>): Result {
        val panels = mutableSetOf<Panel>()
        var minX: Int = Int.MAX_VALUE
        var maxX: Int = Int.MIN_VALUE
        var minY: Int = Int.MAX_VALUE
        var maxY: Int = Int.MIN_VALUE
        var minZ: Int = Int.MAX_VALUE
        var maxZ: Int = Int.MIN_VALUE
        val points = lines
            .map { it.split(",") }
            .map { it.map { i -> i.toInt() } }
            .map { Point3d(it[0], it[1], it[2]) }
            .map { p ->
                maxX = max(maxX, p.x)
                minX = min(minX, p.x)
                maxY = max(maxY, p.y)
                minY = min(minY, p.y)
                maxZ = max(maxZ, p.z)
                minZ = min(minZ, p.z)
                p
            }
            .toSet()
        points
            .flatMap { it.getPanels() }
            .map {
                assert(it.normalVector.normalized.manhattenDistance == 1)
                it
            }
            .forEach { panel ->
                if (panels.contains(panel)) { // if the panel is already in the set, it's not a surface
//                    println("$panel is equal to ${panels.find { it == panel }}")
                    panels.remove(panel)
                } else {
                    panels.add(panel)
                }
            }
        val surface = panels.size

        // ensure we have empty space around our drop
        minX = minX - 1
        maxX = maxX + 1
        minY = minY - 1
        maxY = maxY + 1
        minZ = minZ - 1
        maxZ = maxZ + 1

        // funktionierende Lösung für Part 2: Breitensuche vom Eck aus
        var exterior = 0
        val visited = mutableSetOf<Point3d>()
        val queue = mutableListOf<Point3d>()
        queue.add(Point3d(minX, minY, minZ))
        while (queue.isNotEmpty()) {
            val point = queue.removeAt(0)
            if (point in visited) {
                continue
            }
            visited.add(point)
            for (neighbour in point.getNeighbours()) {
                if (neighbour.x < minX || neighbour.x > maxX || neighbour.y < minY || neighbour.y > maxY || neighbour.z < minZ || neighbour.z > maxZ) {
                    continue
                }
                if (neighbour !in visited) {
                    if (neighbour in points) {
                        exterior++
                    } else {
                        queue.add(neighbour)
                    }
                }
            }
        }

        return Result(surface, exterior)

        // Holzweg: Wenn nur eine Kante zwischen innen und aussen die Grenze ist, hat die Innen- und ide Aussen-Oberfläche
        // eine Verbindung nach meiner Logik (weil ich darauf vertraue, dass diese Panels de-dupliziert werden.
        // Außerdem könnte ich in Grenzfällen nicht unterscheiden, welche Surface innen oder aussen ist, weil ich
        // nur die Nezte aufspanne (und dann halt vermuten müsste, dass die größte die äußere ist...)
//        val panelNodes = panels.map { PanelNode(it) }
//        panelNodes.forEach { node ->
//            panelNodes.forEach { n2 ->
//                if (node.panel != n2.panel && node.panel.touches(n2.panel)) {
//                    if (node.panel.orientation == n2.panel.orientation.opposite()) {
//                        println("${node.panel} and ${n2.panel} touch each other but are opposites")
//                    } else {
//                        node.neighbors.add(n2)
//                    }
//                }
//            }
//        }
//        val unknownNodes = panelNodes.toMutableSet()
//        val surfaces = mutableSetOf<Set<PanelNode>>()
//        while (unknownNodes.isNotEmpty()) {
//            val queue = mutableSetOf<PanelNode>()
//            val visited = mutableSetOf<PanelNode>()
//            queue.add(unknownNodes.first())
//            while (queue.isNotEmpty()) {
//                val node = queue.first()
//                queue.remove(node)
//                unknownNodes.remove(node)
//                visited.add(node)
//                for (next in node.neighbors) {
//                    if (next in unknownNodes) {
//                        queue.add(next)
//                    }
//                }
//            }
//            if (visited.isNotEmpty()) {
//                surfaces.add(visited.toSet())
//                visited.clear()
//            }
//        }
//        println("found ${surfaces.size} surfaces with sizes ${surfaces.map { it.size }.joinToString(", ")}")
//        return Result(surface, surfaces.maxOf { it.size })
    }

    private fun Point3d.getNeighbours(): List<Point3d> {
        return listOf(
            this + RIGHT,
            this + LEFT,
            this + UP,
            this + DOWN,
            this + BACK,
            this + FORTH
        )
    }
    private fun Point3d.getPanels(): Collection<Panel> {
        val opposite = this + RIGHT + BACK + UP
        return listOf(
            Panel("${this}-FRONT", PanelOrientation.FRONT, this, RIGHT, UP),
            Panel("${this}-LEFT", PanelOrientation.LEFT, this, BACK, UP),
            Panel("${this}-FLOOR", PanelOrientation.FLOOR, this, BACK, RIGHT),
            Panel("${this}-RIGHT", PanelOrientation.RIGHT, opposite, FORTH, DOWN),
            Panel("${this}-BACK", PanelOrientation.BACK, opposite, LEFT, DOWN),
            Panel("${this}-CEILING", PanelOrientation.CEILING, opposite, LEFT, FORTH)
        )
    }

    class PanelNode(val panel: Panel) {
        val neighbors: MutableSet<PanelNode> = mutableSetOf()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as PanelNode

            if (panel != other.panel) return false

            return true
        }

        override fun hashCode(): Int {
            return panel.hashCode()
        }

        override fun toString(): String {
            return "Node for $panel"
        }
    }

    enum class PanelOrientation {
        FRONT,
        BACK,
        LEFT,
        RIGHT,
        CEILING,
        FLOOR;

        fun opposite(): PanelOrientation {
            return when(this) {
                FRONT -> BACK
                BACK -> FRONT
                LEFT -> RIGHT
                RIGHT -> LEFT
                CEILING -> FLOOR
                FLOOR -> CEILING
            }
        }
    }

    class Panel(val id: String, val orientation: PanelOrientation, val origin: Point3d, v1: Vector3d, v2: Vector3d) {
        val corners: List<Point3d>
        val edges: List<Segment>
        val normalVector: Vector3d
        init {
            if (v1.length == 0 || v2.length == 0 || v1 in v2) {
                throw IllegalArgumentException("need 2 independent vectors, got ${v1}, $v2")
            }
            normalVector = (v1 cross v2).normalized
            edges = listOf(
                Segment(origin, origin + v1),
                Segment(origin, origin + v2),
                Segment(origin + v1, origin + v1 + v2),
                Segment(origin + v2, origin + v1 + v2)
            ).sorted()
            corners = listOf(
                origin,
                origin + v1,
                origin + v2,
                origin + v1 + v2
            ).sorted()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Panel

            if (edges.size != other.edges.size) return false
            if (corners != other.corners) return false

            return true
        }

        fun touches(other: Panel): Boolean {
            return edges.intersect(other.edges.toSet()).size == 1
        }

        override fun hashCode(): Int {
            return corners.hashCode()
        }

        override fun toString(): String {
            return "$id"
        }
    }

    data class Result(
        val surface: Int,
        val exteriorSurface: Int
    ) {
        override fun toString(): String {
            return "surface area is $surface, exterior surface is $exteriorSurface"
        }
    }
}
