package de.linkel.aoc.utils.grid

import java.lang.IllegalArgumentException

class Grid<T: Any>(
    initWidth: Int = 0,
    initHeight: Int = 0
) {
    companion object {
        fun <T: Any> parse(lines: Sequence<String>, lambda: (pos: Point, c: Char) -> T): Grid<T> {
            val grid = Grid<T>()
            lines
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .forEachIndexed { y, line ->
                    grid.resize(grid.width, y+1)
                    val chars = line
                        .toCharArray()
                    if (chars.size > grid.width) {
                        grid.resize(chars.size, grid.height)
                    }
                    chars
                        .forEachIndexed { x, c ->
                            val p = Point(x, y)
                            grid[p] = lambda(p, c)
                        }
                }
            return grid
        }
    }

    private val store = mutableMapOf<Point, T>()
    // evtl nen performance-optimierteren zugriff? / ne liste aller belegten punkte pro row/col?
    var width = initWidth
        private set
    var height = initHeight
        private set
    val size get(): Int = store.size
    val maxSize get(): Int = width * height

    fun resize(width: Int, height: Int) {
        if (this.width > width && this.height > height) {
            val iterator = store.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.key.x >= width || entry.key.y >= height) {
                    iterator.remove()
                }
            }
        }
        this.width = width
        this.height = height
    }
    fun stretchTo(point: Point) {
        if (point.x >= width) {
            this.width = point.x + 1
        }
        if (point.y >= height) {
            this.height = point.y + 1
        }
    }

    private fun checkPoint(point: Point) {
        if (point.x < 0 || point.y < 0 || point.x >= width || point.y >= height) {
            throw IllegalArgumentException("coordinates $point out of bounds (${width}x$height)")
        }
    }

    operator fun contains(point: Point): Boolean {
        return point.x >= 0 && point.y >= 0 && point.x < width && point.y < height
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
        return Area(minX, minY, maxX - minX, maxY - minY)
    }

    @Suppress("unused")
    fun getRow(y: Int): List<DataPoint<T?>> {
        return List(width) { x ->
            val p = Point(x, y)
            DataPoint(p, store[p])
        }
    }

    fun getRowData(y: Int): List<DataPoint<T>> {
        val result = mutableListOf<DataPoint<T>>()
        for (x in 0 until width) {
            val p = Point(x, y)
            val t = store[p]
            if (t != null) {
                result.add(DataPoint(p, t))
            }
        }
        return result
    }

    @Suppress("unused")
    fun getCol(x: Int): List<DataPoint<T?>> {
        return List(height) { y ->
            val p = Point(x, y)
            DataPoint(p, store[p])
        }
    }

    fun getColData(x: Int): List<DataPoint<T>> {
        val result = mutableListOf<DataPoint<T>>()
        for (y in 0 until height) {
            val p = Point(x, y)
            val t = store[p]
            if (t != null) {
                result.add(DataPoint(p, t))
            }
        }
        return result
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
        return Grid<R>(width, height)
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

    @Suppress("unused")
    fun copy(): Grid<T> {
        return Grid<T>(width, height)
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
