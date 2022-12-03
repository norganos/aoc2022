package de.linkel.aoc.utils

class TopList<T>(
    private val capacity: Int,
    initial: Collection<T> = emptyList()
) : Collection<T>
        where T : Comparable<T>, T: Any
{
    private val store: List<T>

    init {
        store = initial
            .sortedDescending()
            .let {
                if (it.size > capacity)
                    it.subList(0, capacity)
                else
                    it
            }
            .toList()
    }

    override val size: Int
        get() = store.size

    override fun isEmpty(): Boolean {
        return store.isEmpty()
    }

    override fun iterator(): Iterator<T> {
        return store.iterator()
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return store.containsAll(elements)
    }

    override fun contains(element: T): Boolean {
        return store.contains(element)
    }

    operator fun plus(other: Collection<T>): TopList<T> {
        return TopList(
            capacity = capacity,
            initial = store + other.filter { it > store.last() }
        )
    }

    operator fun plus(element: T): TopList<T> {
        return TopList(
            capacity = capacity,
            initial = store + listOf(element)
        )
    }
}
