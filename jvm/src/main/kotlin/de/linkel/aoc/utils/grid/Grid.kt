package de.linkel.aoc.utils.grid

import java.lang.IllegalArgumentException

class Grid<T: Any>(
    origin: Point = Point(0,0),
    dimension: Dimension = Dimension(0,0)
) {
    companion object {
        fun <T: Any> parse(lines: Sequence<String>, lambda: (pos: Point, c: Char) -> T?): Grid<T> {
            val grid = Grid<T>()
            lines
                .filter { it.isNotEmpty() }
                .forEachIndexed { y, line ->
                    val chars = line
                        .toCharArray()
                    chars
                        .forEachIndexed { x, c ->
                            val p = Point(x, y)
                            grid.stretchTo(p)
                            val t = lambda(p, c)
                            if (t != null) {
                                grid[p] = t
                            }
                        }
                }
            grid.crop()
            return grid
        }
    }

    var area = Area(
        x = origin.x,
        y = origin.y,
        width = dimension.width,
        height = dimension.height
    )

    // evtl nen performance-optimierteren zugriff? / ne liste aller belegten punkte pro row/col?
    private val store = mutableMapOf<Point, T>()
    val width get(): Int = area.width
    val height get(): Int = area.height
    val maxSize get(): Int = area.width * area.height

    val size get(): Int = store.size

    fun crop() {
        area = getDataBoundingBox()
    }
    fun stretchTo(point: Point) {
        area = area.extendTo(point)
    }

    private fun checkPoint(point: Point) {
        if (point !in area) {
            throw IllegalArgumentException("coordinates $point out of bounds ($area)")
        }
    }

    operator fun contains(point: Point): Boolean {
        return point in area
    }

    operator fun get(pos: Point): T? {
        checkPoint(pos)
        return store[pos]
    }

    operator fun set(pos: Point, value: T?) {
        checkPoint(pos)
        if (value == null) {
            store.remove(pos)
        } else {
            store[pos] = value
        }
    }

    fun getDataBoundingBox(): Area {
        val minX = store.keys.minOf { it.x }
        val minY = store.keys.minOf { it.y }
        val maxX = store.keys.maxOf { it.x }
        val maxY = store.keys.maxOf { it.y }
        return Area(minX, minY, maxX - minX + 1, maxY - minY + 1)
    }

    @Suppress("unused")
    fun getRow(y: Int): List<DataPoint<T?>> {
        return List(area.width) { dx ->
            val p = Point(area.x + dx, y)
            DataPoint(p, store[p])
        }
    }

    fun getRowData(y: Int): List<DataPoint<T>> {
        return List(area.width) { dx ->
                Point(area.x + dx, y)
            }
            .filter { store[it] != null }
            .map { DataPoint(it, store[it]!!) }
            .toList()
    }

    @Suppress("unused")
    fun getCol(x: Int): List<DataPoint<T?>> {
        return List(area.height) { dy ->
            val p = Point(x, area.y + dy)
            DataPoint(p, store[p])
        }
    }

    fun getColData(x: Int): List<DataPoint<T>> {
        return List(area.height) { dy ->
                Point(x, area.y + dy)
            }
            .filter { store[it] != null }
            .map { DataPoint(it, store[it]!!) }
            .toList()
    }

    fun getBeams(pos: Point): List<List<DataPoint<T>>> {
        val row = getRowData(pos.y)
        val col = getColData(pos.x)
        return listOf(
            col.filter { it.point.y < pos.y }.sortedByDescending { it.point.y },
            row.filter { it.point.x > pos.x }.sortedBy { it.point.x },
            col.filter { it.point.y > pos.y }.sortedBy { it.point.y },
            row.filter { it.point.x < pos.x }.sortedByDescending { it.point.x }
        )
    }

    @Suppress("unused")
    fun getAllData(): List<DataPoint<T>> {
        return store.entries
            .map {
                DataPoint(it.key, it.value)
            }
    }

    fun <R: Any> transform(lambda: (pos: Point, data: T) -> R?): Grid<R> {
        return Grid<R>(area.origin, area.dimension)
            .let { other ->
                store.entries.forEach { entry ->
                    val r = lambda(entry.key, entry.value)
                    if (r != null) {
                        other.store[entry.key] = r
                    }
                }
                other
            }
    }

    fun <R: Any> transformComplete(lambda: (points: Map<Point, T>) -> Map<Point, R>): Grid<R> {
        return Grid<R>(area.origin, area.dimension)
            .let { other ->
                other.store.putAll(lambda(store))
                other
            }
    }

    @Suppress("unused")
    fun copy(): Grid<T> {
        return Grid<T>(area.origin, area.dimension)
            .let { other ->
                store.entries.forEach { entry ->
                    other.store[entry.key] = entry.value
                }
                other
            }
    }

    fun filterData(lambda: (pos: Point, data: T) -> Boolean): List<DataPoint<T>> {
        return store.entries
            .filter { lambda(it.key, it.value) }
            .map {
                DataPoint(it.key, it.value)
            }
    }

    @Suppress("unused")
    fun isNotEmpty(): Boolean {
        return store.isNotEmpty()
    }

    @Suppress("unused")
    fun isEmpty(): Boolean {
        return store.isEmpty()
    }

    private val directions4 = listOf(
        Vector(1, 0),
        Vector(0, 1),
        Vector(-1, 0),
        Vector(0, -1)
    )
    private val directions8 = listOf(
        Vector(1, 0),
        Vector(1, 1),
        Vector(0, 1),
        Vector(-1, 1),
        Vector(-1, 0),
        Vector(-1, -1),
        Vector(0, -1),
        Vector(1, -1)
    )

    fun getNeighbours(point: Point, diagonal: Boolean = false): List<DataPoint<T>> {
        return (if (diagonal) directions8 else directions4)
            .map { point + it }
            .filter { store.containsKey(it) }
            .map { DataPoint(it, store[it]!!)}
    }

    fun dijkstra(start: Point, isDest: (point: DataPoint<T>) -> Boolean, getNeighbours: (from: DataPoint<T>) -> Collection<Point>): List<DataPoint<T>>? {
        val max = this.maxSize + 1
        val weightMap = transform { p, d -> DijkstraNode(d, if (p == start) 0 else max, null) }
        val points = weightMap.getAllData().map { it.point }.toMutableSet()
        var dest: Point? = null
        while (points.isNotEmpty()) {
            val point = points.minBy {  weightMap[it]!!.distance }
            val pointWeightData = weightMap[point]!!
            val dataPoint = DataPoint(point, pointWeightData.data)
            points.remove(point)
            getNeighbours(dataPoint)
                .filter { it in weightMap }
                .filter { it in points }
                .forEach {
                    weightMap[it] = weightMap[it]!!.copy(distance = pointWeightData.distance + 1, before = point)
                }
            if (isDest(dataPoint)) {
                dest = point
                break
            }
        }
        return if (dest != null) {
            var prev: Point? = dest
            val result = mutableListOf<DataPoint<T>>()
            while (prev != null) {
                val prevWeightData = weightMap[prev]!!
                result.add(0, DataPoint(prev, prevWeightData.data))
                prev = prevWeightData.before
            }
            result.toList()
        } else {
            null
        }
    }

    data class DijkstraNode<T>(
        val data: T,
        val distance: Int,
        val before: Point?
    )

}
