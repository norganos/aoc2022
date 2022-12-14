package de.linkel.aoc

import de.linkel.aoc.base.AbstractFileAdventDay
import de.linkel.aoc.utils.grid.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day22Test {
    val aocExample = """
        ...#
        .#..
        #...
        ....
...#.......#
........#...
..#....#....
..........#.
        ...#....
        .....#..
        .#......
        ......#.

10R5L5R10L4R5L5
          """.trimIndent()

    @Test
    fun `example part 1`() {
        assertEquals(
            6032,
            Day22(1).test(aocExample).value
        )
    }

    private fun Day22.TileStep.pointDir(): Pair<Point, Day22.Direction> {
        return Pair(
            Point(this.tile.x, this.tile.y),
            this.direction
        )
    }

    /*
                1111
                1111
                1111
                1111
        222233334444
        222233334444
        222233334444
        222233334444
                55556666
                55556666
                55556666
                55556666
     */

    @Test
    fun `test example cube side connections 1 W - 3 N`() {
        val day = Day22(2)
        aocExample.reader().buffered(1024).use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 1 W <=> 3 N
                assertEquals(Pair(Point(4, 4), Day22.Direction.SOUTH), map[Point(8, 0)]!!.west!!.pointDir())
                assertEquals(Pair(Point(8, 0), Day22.Direction.EAST), map[Point(4, 4)]!!.north!!.pointDir())
                assertEquals(Pair(Point(7, 4), Day22.Direction.SOUTH), map[Point(8, 3)]!!.west!!.pointDir())
                assertEquals(Pair(Point(8, 3), Day22.Direction.EAST), map[Point(7, 4)]!!.north!!.pointDir())
            }
        }
    }

    @Test
    fun `test example cube side connections 1 E - 6 E`() {
        val day = Day22(2)
        aocExample.reader().buffered(1024).use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 1 E <=> 6 E
                assertEquals(Pair(Point(15, 11), Day22.Direction.WEST), map[Point(11, 0)]!!.east!!.pointDir())
                assertEquals(Pair(Point(11, 0), Day22.Direction.WEST), map[Point(15, 11)]!!.east!!.pointDir())
                assertEquals(Pair(Point(15, 8), Day22.Direction.WEST), map[Point(11, 3)]!!.east!!.pointDir())
                assertEquals(Pair(Point(11, 3), Day22.Direction.WEST), map[Point(15, 8)]!!.east!!.pointDir())
            }
        }
    }

    @Test
    fun `test example cube side connections 1 N - 2 N`() {
        val day = Day22(2)
        aocExample.reader().buffered(1024).use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 1 N <=> 2 N
                assertEquals(Pair(Point(3, 4), Day22.Direction.SOUTH), map[Point(8, 0)]!!.north!!.pointDir())
                assertEquals(Pair(Point(8, 0), Day22.Direction.SOUTH), map[Point(3, 4)]!!.north!!.pointDir())
                assertEquals(Pair(Point(0, 4), Day22.Direction.SOUTH), map[Point(11, 0)]!!.north!!.pointDir())
                assertEquals(Pair(Point(11, 0), Day22.Direction.SOUTH), map[Point(0, 4)]!!.north!!.pointDir())
            }
        }
    }

    @Test
    fun `test example cube side connections 2 W - 6 S`() {
        val day = Day22(2)
        aocExample.reader().buffered(1024).use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 2 W <=> 6 S
                assertEquals(Pair(Point(15, 11), Day22.Direction.NORTH), map[Point(0, 4)]!!.west!!.pointDir())
                assertEquals(Pair(Point(0, 4), Day22.Direction.EAST), map[Point(15, 11)]!!.south!!.pointDir())
                assertEquals(Pair(Point(12, 11), Day22.Direction.NORTH), map[Point(0, 7)]!!.west!!.pointDir())
                assertEquals(Pair(Point(0, 7), Day22.Direction.EAST), map[Point(12, 11)]!!.south!!.pointDir())
            }
        }
    }

    @Test
    fun `test example cube side connections 2 S - 5 S`() {
        val day = Day22(2)
        aocExample.reader().buffered(1024).use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 2 S <=> 5 S
                assertEquals(Pair(Point(11, 11), Day22.Direction.NORTH), map[Point(0, 7)]!!.south!!.pointDir())
                assertEquals(Pair(Point(0, 7), Day22.Direction.NORTH), map[Point(11, 11)]!!.south!!.pointDir())
                assertEquals(Pair(Point(8, 11), Day22.Direction.NORTH), map[Point(3, 7)]!!.south!!.pointDir())
                assertEquals(Pair(Point(3, 7), Day22.Direction.NORTH), map[Point(8, 11)]!!.south!!.pointDir())
            }
        }
    }

    @Test
    fun `test example cube side connections 3 S - 5 W`() {
        val day = Day22(2)
        aocExample.reader().buffered(1024).use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 3 S <=> 5 W
                assertEquals(Pair(Point(8, 11), Day22.Direction.EAST), map[Point(4, 7)]!!.south!!.pointDir())
                assertEquals(Pair(Point(4, 7), Day22.Direction.NORTH), map[Point(8, 11)]!!.west!!.pointDir())
                assertEquals(Pair(Point(8, 8), Day22.Direction.EAST), map[Point(7, 7)]!!.south!!.pointDir())
                assertEquals(Pair(Point(7, 7), Day22.Direction.NORTH), map[Point(8, 8)]!!.west!!.pointDir())
            }
        }
    }

    @Test
    fun `test example cube side connections 4 E - 6 N`() {
        val day = Day22(2)
        aocExample.reader().buffered(1024).use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 4 E <=> 6 N
                assertEquals(Pair(Point(15, 8), Day22.Direction.SOUTH), map[Point(11, 4)]!!.east!!.pointDir())
                assertEquals(Pair(Point(11, 4), Day22.Direction.WEST), map[Point(15, 8)]!!.north!!.pointDir())
                assertEquals(Pair(Point(12, 8), Day22.Direction.SOUTH), map[Point(11, 7)]!!.east!!.pointDir())
                assertEquals(Pair(Point(11, 7), Day22.Direction.WEST), map[Point(12, 8)]!!.north!!.pointDir())
            }
        }
    }

    @Test
    fun `example part 2`() {
        assertEquals(
            5031,
            Day22(2).test(aocExample).value
        )
    }

    @Test
    fun `solution part 1`() {
        assertEquals(
            64256,
            Day22(1).solve(emptyList()).value
        )
    }

    @Test
    fun `solution part 2`() {
        assertEquals(
            109224,
            Day22(2).solve(emptyList()).value
        )
    }

    @Test
    fun `test solution cube side connections 1 N - 6 W`() {
        val day = Day22(2)
        AbstractFileAdventDay.from(emptyList<String>(), "input22.txt").use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 1 N <=> 6 W
                assertEquals(Pair(Point(0, 150), Day22.Direction.EAST), map[Point(50, 0)]!!.north!!.pointDir())
                assertEquals(Pair(Point(50, 0), Day22.Direction.SOUTH), map[Point(0, 150)]!!.west!!.pointDir())
                assertEquals(Pair(Point(0, 199), Day22.Direction.EAST), map[Point(99, 0)]!!.north!!.pointDir())
                assertEquals(Pair(Point(99, 0), Day22.Direction.SOUTH), map[Point(0, 199)]!!.west!!.pointDir())
            }
        }
    }

    @Test
    fun `test solution cube side connections 1 W - 4 W`() {
        val day = Day22(2)
        AbstractFileAdventDay.from(emptyList<String>(), "input22.txt").use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 1 W <=> 4 W
                assertEquals(Pair(Point(0, 100), Day22.Direction.EAST), map[Point(50, 49)]!!.west!!.pointDir())
                assertEquals(Pair(Point(50, 49), Day22.Direction.EAST), map[Point(0, 100)]!!.west!!.pointDir())
                assertEquals(Pair(Point(0, 149), Day22.Direction.EAST), map[Point(50, 0)]!!.west!!.pointDir())
                assertEquals(Pair(Point(50, 0), Day22.Direction.EAST), map[Point(0, 149)]!!.west!!.pointDir())
            }
        }
    }

    @Test
    fun `test solution cube side connections 2 N - 6 S`() {
        val day = Day22(2)
        AbstractFileAdventDay.from(emptyList<String>(), "input22.txt").use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 2 N <=> 6 S
                assertEquals(Pair(Point(0, 199), Day22.Direction.NORTH), map[Point(100, 0)]!!.north!!.pointDir())
                assertEquals(Pair(Point(100, 0), Day22.Direction.SOUTH), map[Point(0, 199)]!!.south!!.pointDir())
                assertEquals(Pair(Point(49, 199), Day22.Direction.NORTH), map[Point(149, 0)]!!.north!!.pointDir())
                assertEquals(Pair(Point(149, 0), Day22.Direction.SOUTH), map[Point(49, 199)]!!.south!!.pointDir())
            }
        }
    }

    @Test
    fun `test solution cube side connections 2 E - 5 E`() {
        val day = Day22(2)
        AbstractFileAdventDay.from(emptyList<String>(), "input22.txt").use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 2 E <=> 5 E
                assertEquals(Pair(Point(99, 149), Day22.Direction.WEST), map[Point(149, 0)]!!.east!!.pointDir())
                assertEquals(Pair(Point(149, 0), Day22.Direction.WEST), map[Point(99, 149)]!!.east!!.pointDir())
                assertEquals(Pair(Point(99, 100), Day22.Direction.WEST), map[Point(149, 49)]!!.east!!.pointDir())
                assertEquals(Pair(Point(149, 49), Day22.Direction.WEST), map[Point(99, 100)]!!.east!!.pointDir())
            }
        }
    }

    @Test
    fun `test solution cube side connections 2 S - 3 E`() {
        val day = Day22(2)
        AbstractFileAdventDay.from(emptyList<String>(), "input22.txt").use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 2 S <=> 3 E
                assertEquals(Pair(Point(99, 99), Day22.Direction.WEST), map[Point(149, 49)]!!.south!!.pointDir())
                assertEquals(Pair(Point(149, 49), Day22.Direction.NORTH), map[Point(99, 99)]!!.east!!.pointDir())
                assertEquals(Pair(Point(99, 50), Day22.Direction.WEST), map[Point(100, 49)]!!.south!!.pointDir())
                assertEquals(Pair(Point(100, 49), Day22.Direction.NORTH), map[Point(99, 50)]!!.east!!.pointDir())
            }
        }
    }

    @Test
    fun `test solution cube side connections 3 W - 4 N`() {
        val day = Day22(2)
        AbstractFileAdventDay.from(emptyList<String>(), "input22.txt").use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 3 W <=> 4 N
                assertEquals(Pair(Point(49, 100), Day22.Direction.SOUTH), map[Point(50, 99)]!!.west!!.pointDir())
                assertEquals(Pair(Point(50, 99), Day22.Direction.EAST), map[Point(49, 100)]!!.north!!.pointDir())
                assertEquals(Pair(Point(0, 100), Day22.Direction.SOUTH), map[Point(50, 50)]!!.west!!.pointDir())
                assertEquals(Pair(Point(50, 50), Day22.Direction.EAST), map[Point(0, 100)]!!.north!!.pointDir())
            }
        }
    }

    @Test
    fun `test solution cube side connections 5 S - 6 E`() {
        val day = Day22(2)
        AbstractFileAdventDay.from(emptyList<String>(), "input22.txt").use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

                // 5 S <=> 6 E
                assertEquals(Pair(Point(49, 199), Day22.Direction.WEST), map[Point(99, 149)]!!.south!!.pointDir())
                assertEquals(Pair(Point(99, 149), Day22.Direction.NORTH), map[Point(49, 199)]!!.east!!.pointDir())
                assertEquals(Pair(Point(49, 150), Day22.Direction.WEST), map[Point(50, 149)]!!.south!!.pointDir())
                assertEquals(Pair(Point(50, 149), Day22.Direction.NORTH), map[Point(49, 150)]!!.east!!.pointDir())
            }
        }
    }
}
