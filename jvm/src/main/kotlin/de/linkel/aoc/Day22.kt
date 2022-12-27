package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Area
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import de.linkel.aoc.utils.ring.Ring
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import kotlin.math.max


@Singleton
class Day22(
    @Suppress("MnInjectionPoints") @Value("2") val part: Int = 2,
): AbstractLinesAdventDay<Day22.Result>() {
    override val day = 22

    override fun process(lines: Sequence<String>): Result {
        val (map, instructions) = parse(lines)

        interconnect(map)

        val player = Player(map.getRowData(map.area.y).first().data, Direction.EAST)
        instructions.forEach { instruction ->
            instruction.move(map, player)
            if (player.pos.wall) {
                throw IllegalStateException("standing in a wall...")
            }
        }

        return Result(
            1000 * (player.pos.y + 1) + 4 * (player.pos.x + 1) + player.direction.ordinal
        )
    }

    fun parse(lines: Sequence<String>): Pair<Grid<Tile>, List<Instruction>> {
        val map = Grid<Tile>()
        val instructions = mutableListOf<Instruction>()
        var parseState = 0
        lines.forEachIndexed { y, line ->
            if (line.isBlank()) {
                parseState++
            } else if (parseState == 0) {
                line
                    .toCharArray()
                    .forEachIndexed { x, c ->
                        val p = Point(x, y)
                        map.stretchTo(p)
                        map[p] = when(c) {
                            '.' -> Tile(
                                false,
                                x,
                                y
                            )
                            '#' -> Tile(
                                true,
                                x,
                                y
                            )
                            ' ' -> null
                            else -> throw IllegalArgumentException("unknown char '$c'")
                        }
                    }
            } else {
                val buffer = StringBuilder()
                line.toCharArray().forEach { c ->
                    if (c.isDigit()) {
                        buffer.append(c)
                    } else if (c == 'R') {
                        if (buffer.isNotEmpty()) {
                            instructions.add(MoveInstruction(buffer.toString().toInt()))
                            buffer.clear()
                        }
                        instructions.add(TurnRightInstruction())
                    } else if (c == 'L') {
                        if (buffer.isNotEmpty()) {
                            instructions.add(MoveInstruction(buffer.toString().toInt()))
                            buffer.clear()
                        }
                        instructions.add(TurnLeftInstruction())
                    }
                }
                if (buffer.isNotEmpty()) {
                    instructions.add(MoveInstruction(buffer.toString().toInt()))
                    buffer.clear()
                }
            }
        }
        map.crop()
        return Pair(map, instructions)
    }

    fun interconnect(map: Grid<Tile>) {
        val box = map.getDataBoundingBox()
        if (part == 1) {
            for (y in (box.y until box.y + box.height)) {
                val cells = map.getRowData(y)
                var prev = cells.last().data
                cells.forEach { cell ->
                    cell.data.west = TileStep(prev, Direction.WEST)
                    prev.east = TileStep(cell.data, Direction.EAST)
                    prev = cell.data
                }
            }
            for (x in (box.x until box.x + box.width)) {
                val cells = map.getColData(x)
                var prev = cells.last().data
                cells.forEach { cell ->
                    cell.data.north = TileStep(prev, Direction.NORTH)
                    prev.south = TileStep(cell.data, Direction.SOUTH)
                    prev = cell.data
                }
            }
        } else {
            for (y in (box.y until box.y + box.height)) {
                val cells = map.getRowData(y)
                var prev = cells.first().data
                cells.drop(1).forEach { cell ->
                    cell.data.west = TileStep(prev, Direction.WEST)
                    prev.east = TileStep(cell.data, Direction.EAST)
                    prev = cell.data
                }
            }
            for (x in (box.x until box.x + box.width)) {
                val cells = map.getColData(x)
                var prev = cells.first().data
                cells.drop(1).forEach { cell ->
                    cell.data.north = TileStep(prev, Direction.NORTH)
                    prev.south = TileStep(cell.data, Direction.SOUTH)
                    prev = cell.data
                }
            }
            // eine wuerfel-topologie muss (glaub ich) auf genau einer dimension aus 4 quadraten bestehen,
            val squareSize = max(map.width, map.height) / 4
            // beim zusammenklappen kommen immer zwei seiten zusammen, die am naechsten sind.
            // -> wir suchen uns erstmal die quadrate
            // -> dann suchen wir nach offenen seiten
            // -> dann finden wir zu einer offenen seite die am naechsten liegende (noch nicht gepairte) andere offene seite (ohne dieselben quadrate mehrfach zu verbinden)
            val quadrants = (0..3)
                .flatMap { y ->
                    (0..3).map { x ->
                        Area(x * squareSize, y * squareSize, squareSize, squareSize)
                    }
                }
                .filter { it.origin in map && map[it.origin] != null }
                .onEachIndexed { i: Int, area: Area -> area.id = "${i+1}" }
            val edges = quadrants
                .flatMap { it.getEdges() }
                .filter { it.neighbourArea !in quadrants }
                .toMutableList()

            assert(edges.size == 14)

            val ring = Ring<PairableEdge>()
            var last = ring.add(PairableEdge(edges.removeAt(0)))
            while (edges.isNotEmpty()) {
                val c = last.payload
                val e = findEdge(edges, listOf(
                    Edge(c.area, c.side.right()),
                    Edge(c.area + (c.side.right().vector * c.area.width), c.side),
                    Edge(c.area + (c.side.right().vector * c.area.width + c.side.vector * c.area.width), c.side.left())
                ))
                edges.remove(e)
                last = last.append(PairableEdge(e))
            }
            val areaConnections = quadrants.associateWith { mutableSetOf<Area>() }
            for (e in ring.ringIterator()) { // als erstes die konkaven ecken finden
                val n = e.next
                if (n.payload.paired || e.payload.paired || e.payload.side == n.payload.side || e.payload.area == n.payload.area) {
                    continue
                }
                n.payload.correspondent = e.payload
                e.payload.correspondent = n.payload
                e.payload.pairDistance = 1
                n.payload.pairDistance = -1
                areaConnections[n.payload.area]!!.add(e.payload.area)
                areaConnections[e.payload.area]!!.add(n.payload.area)
            }
            while (ring.any { !it.paired }) {
                for (e in ring.ringIterator()) {
                    if (e.payload.paired) {
                        continue
                    }
                    val n = e.drop(1).first { !it.payload.paired }
                    if (n.payload.area == e.payload.area || n.payload.area in areaConnections[e.payload.area]!! || e.payload.area in areaConnections[n.payload.area]!!) {
                        continue
                    }
                    if (e.distanceTo(n) == 1 && e.payload.side == n.payload.side) {
                        continue
                    }
                    n.payload.correspondent = e.payload
                    e.payload.correspondent = n.payload
                    e.payload.pairDistance = e.distanceTo(n)
                    n.payload.pairDistance = n.distanceTo(e)
                    areaConnections[n.payload.area]!!.add(e.payload.area)
                    areaConnections[e.payload.area]!!.add(n.payload.area)
                }
            }
            for (elem in ring) {
                for (transition in elem.transitions()) {
                    map[transition.standingAt]!![transition.stepTowards] = TileStep(map[transition.endsUpAt]!!, transition.lookingIn)
                }
            }
        }
    }

    private fun Area.getEdges(): List<Edge> {
        return listOf(
            Edge(this, Direction.NORTH),
            Edge(this, Direction.EAST),
            Edge(this, Direction.SOUTH),
            Edge(this, Direction.WEST)
        )
    }

    private fun findEdge(edges: Collection<Edge>, lookingFor: Collection<Edge>): Edge {
        return edges.first { it in lookingFor }
    }

    data class Edge(
        val area: Area,
        val side: Direction
    ) {
        val neighbourArea = area + (side.vector * area.width)

        override fun toString(): String {
            return "${area.id} $side"
        }
    }
    data class PairableEdge(
        val edge: Edge
    ) {
        val side get(): Direction = edge.side
        val area get(): Area = edge.area
        var correspondent: PairableEdge? = null
        var pairDistance = 0
        val paired get(): Boolean = correspondent != null

        fun points(): List<Point> {
            return when (side) {
                Direction.NORTH -> if (pairDistance < 0) area.northWest .. area.northEast else area.northEast .. area.northWest
                Direction.SOUTH -> if (pairDistance < 0) area.southEast .. area.southWest else area.southWest .. area.southEast
                Direction.EAST -> if (pairDistance < 0) area.northEast .. area.southEast else area.southEast .. area.northEast
                Direction.WEST -> if (pairDistance < 0) area.southWest .. area.northWest else area.northWest .. area.southWest
            }
        }

        fun transitions(): List<TileTransition> {
            return points()
                .zip(correspondent!!.points())
                .map { p ->
                    TileTransition(p.first, side, p.second, correspondent!!.side.opposite())
                }
                .toList()
        }

        override fun toString(): String {
            return if (correspondent != null) {
                "${area.id} $side -> ${correspondent!!.area.id} ${correspondent!!.side}"
            } else {
                "${area.id} $side -> null"
            }
        }
    }

    data class TileTransition(val standingAt: Point, val stepTowards: Direction, val endsUpAt: Point, val lookingIn: Direction)
    interface Instruction {
        fun move(map: Grid<Tile>, player: Player)
    }
    data class MoveInstruction(val steps: Int): Instruction {
        override fun move(map: Grid<Tile>, player: Player) {
            repeat(steps) {
                val next = player.pos.next(player.direction)
                if (next == null) {
                    throw IllegalStateException("did not find a way to move from ${player.pos} towards ${player.direction}")
                } else if (!next.tile.wall) {
                    player.pos = next.tile
                    player.direction = next.direction
                }
            }
        }
    }
    class TurnRightInstruction(): Instruction {
        override fun move(map: Grid<Tile>, player: Player) {
            player.direction = player.direction.right()
        }
    }
    class TurnLeftInstruction(): Instruction {
        override fun move(map: Grid<Tile>, player: Player) {
            player.direction = player.direction.left()
        }
    }

    class Player(
        var pos: Tile,
        var direction: Direction
    )

    data class Tile(
        val wall: Boolean,
        val x: Int,
        val y: Int
    ) {
        var north: TileStep? = null
        var east: TileStep? = null
        var south: TileStep? = null
        var west: TileStep? = null

        fun next(direction: Direction): TileStep? {
            return when(direction) {
                Direction.NORTH -> north
                Direction.EAST -> east
                Direction.SOUTH -> south
                Direction.WEST -> west
            }
        }

        operator fun get(direction: Direction): TileStep {
            return next(direction)!!
        }

        operator fun set(direction: Direction, value: TileStep) {
            when(direction) {
                Direction.NORTH -> north = value
                Direction.EAST -> east = value
                Direction.SOUTH -> south = value
                Direction.WEST -> west = value
            }
        }
    }
    data class TileStep(
        val tile: Tile,
        val direction: Direction
    )

    enum class Direction(val vector: Vector) {
        EAST(Vector(1,0)),
        SOUTH(Vector(0, 1)),
        WEST(Vector(-1, 0)),
        NORTH(Vector(0, -1));


        fun right(): Direction {
            val all = values()
            return all[(this.ordinal + 1) % all.size]
        }

        fun left(): Direction {
            val all = values()
            return all[Math.floorMod(this.ordinal - 1, all.size)]
        }

        fun opposite(): Direction {
            val all = values()
            return all[Math.floorMod(this.ordinal + 2, all.size)]
        }
    }

    data class Result(
        val value: Int
    ) {
        override fun toString(): String {
            return "part1: $value"
        }
    }
}
