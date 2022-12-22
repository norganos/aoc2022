package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
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

        val box = map.getDataBoundingBox()
        if (part == 1) {
            for (y in (box.y until box.y + box.height)) {
                val cells = map.getRowData(y)
                var prev = cells.last().data
                cells.forEach { cell ->
                    cell.data.west = prev
                    prev.east = cell.data
                    prev = cell.data
                }
            }
            for (x in (box.x until box.x + box.width)) {
                val cells = map.getColData(x)
                var prev = cells.last().data
                cells.forEach { cell ->
                    cell.data.north = prev
                    prev.south = cell.data
                    prev = cell.data
                }
            }
        } else {
            for (y in (box.y until box.y + box.height)) {
                val cells = map.getRowData(y)
                var prev = cells.first().data
                cells.drop(1).forEach { cell ->
                    cell.data.west = prev
                    prev.east = cell.data
                    prev = cell.data
                }
            }
            for (x in (box.x until box.x + box.width)) {
                val cells = map.getColData(x)
                var prev = cells.first().data
                cells.drop(1).forEach { cell ->
                    cell.data.north = prev
                    prev.south = cell.data
                    prev = cell.data
                }
            }
        }

        val player = Player(map.getRowData(box.y).first().data, Direction.EAST)
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

    data class CubePaneSwitch(val x: Int, val y: Int, val direction: Direction)
    interface Instruction {
        fun move(map: Grid<Tile>, player: Player)
    }
    data class MoveInstruction(val steps: Int): Instruction {
        override fun move(map: Grid<Tile>, player: Player) {
            val squareSize = max(map.width, map.height) / 4
            repeat(steps) {
                val next = player.pos.next(player.direction)
                if (next == null) {
                    val x = player.pos.x
                    val y = player.pos.y

                    // ich hasse mich für die nächsten 80 Zeilen...
                    val turned: CubePaneSwitch? = if (squareSize == 50) {
                        if (x in 50..99 && y in 0..49) { // square 1
                            when(player.direction) {
                                Direction.NORTH -> CubePaneSwitch(0, 150 + (x - 50), Direction.EAST)
                                Direction.WEST -> CubePaneSwitch(0, 100 + (49 - y), Direction.EAST)
                                else -> null
                            }
                        } else if (x in 100..149 && y in 0..49) {
                            when(player.direction) {
                                Direction.NORTH -> CubePaneSwitch(x - 100,199, Direction.NORTH)
                                Direction.EAST -> CubePaneSwitch(99, 100 + (49 - y), Direction.WEST)
                                Direction.SOUTH -> CubePaneSwitch(99, 50 + (x - 100), Direction.WEST)
                                else -> null
                            }
                        } else if (x in 50..99 && y in 50..99) {
                            when(player.direction) {
                                Direction.WEST -> CubePaneSwitch(y - 50,100, Direction.SOUTH)
                                Direction.EAST -> CubePaneSwitch(y + 50,49, Direction.NORTH)
                                else -> null
                            }
                        } else if (x in 0..49 && y in 100..149) {
                            when(player.direction) {
                                Direction.WEST -> CubePaneSwitch(50, (149 - y), Direction.EAST)
                                Direction.NORTH -> CubePaneSwitch(50, x + 50, Direction.EAST)
                                else -> null
                            }
                        } else if (x in 50..99 && y in 100..149) {
                            when(player.direction) {
                                Direction.EAST -> CubePaneSwitch(149, 149 - y, Direction.WEST)
                                Direction.SOUTH -> CubePaneSwitch(49, 150 + (x - 50), Direction.WEST)
                                else -> null
                            }
                        } else if (x in 0..49 && y in 150..199) {
                            when(player.direction) {
                                Direction.WEST -> CubePaneSwitch(50 + (y - 150), 0, Direction.SOUTH)
                                Direction.EAST -> CubePaneSwitch(50 + (y - 150), 149, Direction.NORTH)
                                Direction.SOUTH -> CubePaneSwitch(100 + x, 0, Direction.SOUTH)
                                else -> null
                            }
                        } else null
                    } else if (squareSize == 4) {
                        if (x in 8..11 && y in 0..3) { // square 1
                            when(player.direction) {
                                Direction.WEST -> CubePaneSwitch(y + 4, 4, Direction.SOUTH)
                                Direction.EAST -> CubePaneSwitch(15, 11 - y, Direction.WEST)
                                Direction.NORTH -> CubePaneSwitch(3 - (x - 8), 4, Direction.SOUTH)
                                else -> null
                            }
                        } else if (x in 0..3 && y in 4..7) {
                            when(player.direction) {
                                Direction.NORTH -> CubePaneSwitch(8 + (3 - x), 0, Direction.SOUTH)
                                Direction.WEST -> CubePaneSwitch(15 - (y - 4), 11, Direction.NORTH)
                                Direction.SOUTH -> CubePaneSwitch(8 + (3 - x), 11, Direction.NORTH)
                                else -> null
                            }
                        } else if (x in 4..7 && y in 4..7) {
                            when(player.direction) {
                                Direction.NORTH -> CubePaneSwitch(8, x - 4, Direction.EAST)
                                Direction.SOUTH -> CubePaneSwitch(8, 8 + (7 - x), Direction.EAST)
                                else -> null
                            }
                        } else if (x in 8..11 && y in 4..7) {
                            when(player.direction) {
                                Direction.EAST -> CubePaneSwitch(12 + (7 - y), 8, Direction.SOUTH)
                                else -> null
                            }
                        } else if (x in 8..11 && y in 8..11) {
                            when(player.direction) {
                                Direction.WEST -> CubePaneSwitch(7 - (y - 8), 7, Direction.NORTH)
                                Direction.SOUTH -> CubePaneSwitch(3 - (x - 8), 7, Direction.NORTH)
                                else -> null
                            }
                        } else if (x in 12..15 && y in 8..11) {
                            when(player.direction) {
                                Direction.NORTH -> CubePaneSwitch(11, 4 + (15 - x), Direction.WEST)
                                Direction.EAST -> CubePaneSwitch(11, 11 - y, Direction.WEST)
                                Direction.SOUTH -> CubePaneSwitch(0, 4 + (15 - x), Direction.EAST)
                                else -> null
                            }
                        } else null
                    } else null
                    if (turned == null) {
                        throw IllegalStateException("did not find a way to move from ${player.pos} towards ${player.direction}")
                    }
                    val p = Point(turned.x, turned.y)
                    if (map[p] == null) {
                        throw IllegalStateException("calculcated wrong coordinates for move from ${player.pos} towards ${player.direction}: ${p} is empty")
                    }
                    if (!map[p]!!.wall) {
                        player.pos = map[p]!!
                        player.direction = turned.direction
                    }
                } else if (!next.wall) {
                    player.pos = next
                }
            }
        }
    }
    class TurnRightInstruction(): Instruction {
        override fun move(map: Grid<Tile>, player: Player) {
            player.direction = player.direction.turnRight()
        }
    }
    class TurnLeftInstruction(): Instruction {
        override fun move(map: Grid<Tile>, player: Player) {
            player.direction = player.direction.turnLeft()
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
        var north: Tile? = null
        var east: Tile? = null
        var south: Tile? = null
        var west: Tile? = null

        fun next(direction: Direction): Tile? {
            return when(direction) {
                Direction.NORTH -> north
                Direction.EAST -> east
                Direction.SOUTH -> south
                Direction.WEST -> west
            }
        }
    }

    enum class Direction {
        EAST,
        SOUTH,
        WEST,
        NORTH;

        fun turnRight(): Direction {
            val all = values()
            return all[(this.ordinal + 1) % all.size]
        }

        fun turnLeft(): Direction {
            val all = values()
            return all[Math.floorMod(this.ordinal - 1, all.size)]
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
