package de.linkel.aoc

import de.linkel.aoc.utils.grid.Point
import org.junit.jupiter.api.Test
import kotlin.math.min
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

    @Test
    fun `test part2 cube side connections`() {
        val day = Day22(2)
        aocExample.reader().buffered(1024).use { reader ->
            reader.useLines { lines ->
                val (map, _) = day.parse(lines)
                day.interconnect(map)

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

                // 1 <=> 3
                assertEquals(Pair(Point(4, 4), Day22.Direction.SOUTH), map[Point(8, 0)]!!.west!!.pointDir())
                assertEquals(Pair(Point(8, 0), Day22.Direction.EAST), map[Point(4, 4)]!!.north!!.pointDir())
                assertEquals(Pair(Point(7, 4), Day22.Direction.SOUTH), map[Point(8, 3)]!!.west!!.pointDir())
                assertEquals(Pair(Point(8, 3), Day22.Direction.EAST), map[Point(7, 4)]!!.north!!.pointDir())

                // 1 <=> 6
                assertEquals(Pair(Point(15, 11), Day22.Direction.WEST), map[Point(11, 0)]!!.east!!.pointDir())
                assertEquals(Pair(Point(11, 0), Day22.Direction.WEST), map[Point(15, 11)]!!.east!!.pointDir())
                assertEquals(Pair(Point(15, 8), Day22.Direction.WEST), map[Point(11, 3)]!!.east!!.pointDir())
                assertEquals(Pair(Point(11, 3), Day22.Direction.WEST), map[Point(15, 8)]!!.east!!.pointDir())
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
            0,
            Day22().solve(emptyList()).value
        )
    }
}
