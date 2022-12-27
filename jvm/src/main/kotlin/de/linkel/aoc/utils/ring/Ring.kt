package de.linkel.aoc.utils.ring

import java.lang.IllegalStateException
import java.util.ConcurrentModificationException
import kotlin.NoSuchElementException

class Ring<T>: Collection<T> {
    var store = mutableSetOf<RingElement<T>>()
    var counter = 0
    var modificiationCounter = 0

    val elements get(): Iterable<RingElement<T>> = object: Iterable<RingElement<T>> {
        override fun iterator(): Iterator<RingElement<T>> {
            return ringIterator()
        }
    }

    fun add(item: T): RingElement<T> {
        val newElement = RingElement(this, counter++, item)
        if (store.isNotEmpty()) {
            val someElement = store.last()
            newElement.next = someElement.next
            newElement.prev = someElement
            someElement.next.prev = newElement
            someElement.next = newElement
        }
        store.add(newElement)
        modificiationCounter++
        return newElement
    }

    fun insertAfter(item: T, referenceElement: RingElement<T>): RingElement<T> {
        if (referenceElement !in store) {
            throw IllegalArgumentException("provided reference element not found in ring")
        }
        val newElement = RingElement(this, counter++, item)
        newElement.next = referenceElement.next
        newElement.prev = referenceElement
        referenceElement.next.prev = newElement
        referenceElement.next = newElement
        store.add(newElement)
        modificiationCounter++
        return newElement
    }

    fun first(): RingElement<T> {
        return store.first()
    }

    fun remove(item: T): Boolean {
        val elem = store.firstOrNull { it.payload == item }
        return if (elem != null) {
            return remove(elem)
        } else false
    }
    fun remove(elem: RingElement<T>): Boolean {
        elem.next.prev = elem.prev
        elem.prev.next = elem.next
        val result = store.remove(elem)
        modificiationCounter++
        elem.dead = true
        return result
    }

    class RingElement<T>(
        val ring: Ring<T>,
        val idx: Int,
        val payload: T,
        prev: RingElement<T>? = null,
        next: RingElement<T>? = null
    ): Iterable<RingElement<T>> {
        var prev = prev ?: this
        var next = next ?: this
        var dead = false
        override fun toString(): String {
            return "$payload"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as RingElement<*>

            if (idx != other.idx) return false
            if (payload != other.payload) return false

            return true
        }

        override fun hashCode(): Int {
            var result = idx
            result = 31 * result + (payload?.hashCode() ?: 0)
            return result
        }

        fun remove() {
            if (dead) {
                throw IllegalStateException("element got removed from ring")
            }
            ring.remove(this)
        }

        fun append(item: T): RingElement<T> {
            if (dead) {
                throw IllegalStateException("element got removed from ring")
            }
            return ring.insertAfter(item, this)
        }

        fun moveRight() {
            if (dead) {
                throw IllegalStateException("element got removed from ring")
            }
            val oldNext = next
            next = next.next
            next.prev = this
            oldNext.next = this
            prev.next = oldNext
            oldNext.prev = prev
            prev = oldNext
            ring.modificiationCounter++
        }

        fun moveRight(steps: Int) {
            repeat(Math.floorMod(steps, ring.size)) {
                moveRight()
            }
        }

        fun moveLeft() {
            if (dead) {
                throw IllegalStateException("element got removed from ring")
            }
            val oldPrev = prev
            prev = prev.prev
            prev.next = this
            oldPrev.prev = this
            next.prev = oldPrev
            oldPrev.next = next
            next = oldPrev
            ring.modificiationCounter++
        }

        fun moveLeft(steps: Int) {
            repeat(Math.floorMod(steps, ring.size)) {
                moveLeft()
            }
        }

        override fun iterator(): Iterator<RingElement<T>> {
            if (dead) {
                throw IllegalStateException("element got removed from ring")
            }
            return RingIterator(this) { it }
        }

        fun payloadIterator(): Iterator<T> {
            if (dead) {
                throw IllegalStateException("element got removed from ring")
            }
            return RingIterator(this) { it.payload }
        }

        operator fun get(steps: Int): RingElement<T> {
            if (dead) {
                throw IllegalStateException("element got removed from ring")
            }
            var result = this
            var togo = steps % ring.size
            while (togo > 0) {
                result = result.next
                togo--
            }
            while (togo < 0) {
                result = result.prev
                togo++
            }
            return result
        }

        fun distanceTo(other: RingElement<T>): Int {
            if (dead) {
                throw IllegalStateException("element got removed from ring")
            }
            var left = this
            var right = this
            var distance = 0
            while (distance < ring.size) {
                if (right == other) {
                    return distance
                }
                if (left == other) {
                    return -distance
                }
                left = left.prev
                right = right.next
                distance++
            }
            throw NoSuchElementException()
        }
    }

    override val size: Int
        get() = store.size

    override fun isEmpty(): Boolean = store.isEmpty()

    override fun iterator(): Iterator<T> = if (store.isEmpty()) emptyList<T>().iterator() else RingIterator(store.first()) { it.payload }

    fun ringIterator(): Iterator<RingElement<T>> = if (store.isEmpty()) emptyList<RingElement<T>>().iterator() else RingIterator(store.first()) { it }

    override fun containsAll(elements: Collection<T>): Boolean = this.store.map { it.payload }.toSet().containsAll(elements)

    override fun contains(element: T): Boolean = store.any { it.payload == element }

    class RingIterator<R,T>(val startElement: RingElement<T>, val getter: (element: RingElement<T>) -> R): Iterator<R> {
        var nextElement = startElement
        var modificiationCounter = startElement.ring.modificiationCounter
        var pristine = true
        override fun hasNext(): Boolean {
            return if (pristine) true else nextElement !== startElement
        }

        override fun next(): R {
            if (nextElement.ring.modificiationCounter != modificiationCounter) {
                throw ConcurrentModificationException()
            }
            val currentElement = nextElement
            if (!pristine && currentElement === startElement) {
                throw NoSuchElementException()
            }
            pristine = false
            nextElement = currentElement.next
            return getter(currentElement)
        }

    }
}