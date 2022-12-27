package de.linkel.aoc.utils.ring

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import java.util.ConcurrentModificationException
import java.util.NoSuchElementException
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class RingTest {

    @Test
    fun `new ring is empty`() {
        val ring = Ring<String>()
        assertTrue(ring.isEmpty())
        assertFalse(ring.isNotEmpty())
        assertEquals(0, ring.size)
    }

    @Test
    fun `iterator of empty ring has no next`() {
        val ring = Ring<String>()
        assertEquals(false, ring.iterator().hasNext())
        assertThrows<NoSuchElementException> { ring.iterator().next() }
    }

    @Test
    fun `empty ring does not contain any element`() {
        val ring = Ring<String>()
        assertFalse(ring.contains("a"))
    }

    @Test
    fun `if we add a element, the ring is not empty any more`() {
        val ring = Ring<String>()
        ring.add("a")
        assertFalse(ring.isEmpty())
        assertTrue(ring.isNotEmpty())
        assertEquals(1, ring.size)
        assertEquals(true, ring.iterator().hasNext())
    }

    @Test
    fun `if we add a element, it is contained`() {
        val ring = Ring<String>()
        ring.add("a")
        assertTrue(ring.contains("a"))
    }

    @Test
    fun `if we add a element, it points to itself`() {
        val ring = Ring<String>()
        ring.add("a")
        val elem = ring.first()
        assertEquals("a", elem.payload)
        assertEquals(elem, elem.next)
        assertEquals(elem, elem.prev)
    }

    @Test
    fun `if we add a element, the iterator returns only that element`() {
        val ring = Ring<String>()
        ring.add("a")
        val iterator = ring.iterator()
        assertEquals(true, iterator.hasNext())
        assertEquals("a", iterator.next())
        assertEquals(false, iterator.hasNext())
        assertThrows<NoSuchElementException> { iterator.next() }
    }

    @Test
    fun `if we remove the last element, the ring is empty again`() {
        val ring = Ring<String>()
        ring.add("a")
        assertTrue(ring.remove("a"))
        assertTrue(ring.isEmpty())
        assertFalse(ring.isNotEmpty())
        assertEquals(0, ring.size)
    }

    @Test
    fun `if we try to remove something that is not in the ring, nothing happens`() {
        val ring = Ring<String>()
        ring.add("a")
        assertFalse(ring.remove("b"))
        assertTrue(ring.isNotEmpty())
        assertFalse(ring.isEmpty())
        assertEquals(1, ring.size)
    }

    @Test
    fun `if we add two elements they point to each other`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        assertEquals(2, ring.size)
        assertEquals(a, b.next)
        assertEquals(a, b.prev)
        assertEquals(b, a.next)
        assertEquals(b, a.prev)
    }

    @Test
    fun `if we add two elements the iterator returns both`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        assertEquals(setOf("a", "b"), ring.iterator().toList().toSet())
        assertEquals(listOf("a", "b"), a.iterator().toList().map { it.payload })
        assertEquals(listOf("b", "a"), b.iterator().toList().map { it.payload })
    }

    @Test
    fun `if we remove one of two elements the ring only contains the remaining one`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        b.remove()
        assertEquals(1, ring.size)
        assertEquals(listOf("a"), ring.iterator().toList())
        assertEquals(listOf("a"), a.iterator().toList().map { it.payload })
        assertThrows<IllegalStateException> { b.iterator() }
    }

    @Test
    fun `if the have many elements, we can traverse`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        val d = ring.add("d")
        assertEquals(b, a.next)
        assertEquals(c, b.next)
        assertEquals(d, c.next)
        assertEquals(a, d.next)
        assertEquals(d, a.prev)
        assertEquals(a, b.prev)
        assertEquals(b, c.prev)
        assertEquals(c, d.prev)
        assertEquals(b, a[1])
        assertEquals(d, a[-1])
        assertEquals(c, a[2])
        assertEquals(a, a[4])
        assertEquals(b, a[-3])
        assertEquals(listOf("a", "b", "c", "d"), a.iterator().toList().map { it.payload })
    }

    @Test
    fun `if the have many elements, we can move elements right`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        val d = ring.add("d")
        b.moveRight()
        assertEquals(d, b.next)
        assertEquals(b, d.prev)
        assertEquals(b, c.next)
        assertEquals(c, b.prev)
        assertEquals(listOf("a", "c", "b", "d"), a.iterator().toList().map { it.payload })
        assertEquals(listOf("b", "d", "a", "c"), b.iterator().toList().map { it.payload })
    }

    @Test
    fun `if the have many elements, we can move elements multiple steps to the right`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        ring.add("c")
        val d = ring.add("d")
        b.moveRight(2)
        assertEquals(a, b.next)
        assertEquals(b, a.prev)
        assertEquals(b, d.next)
        assertEquals(d, b.prev)
        assertEquals(listOf("a", "c", "d", "b"), a.iterator().toList().map { it.payload })
        assertEquals(listOf("b", "a", "c", "d"), b.iterator().toList().map { it.payload })
    }

    @Test
    fun `if the have many elements, we can move elements left`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        ring.add("c")
        val d = ring.add("d")
        b.moveLeft()
        assertEquals(a, b.next)
        assertEquals(b, a.prev)
        assertEquals(b, d.next)
        assertEquals(d, b.prev)
        assertEquals(listOf("a", "c", "d", "b"), a.iterator().toList().map { it.payload })
        assertEquals(listOf("b", "a", "c", "d"), b.iterator().toList().map { it.payload })
    }

    @Test
    fun `if the have many elements, we can move elements multiple steps to the left`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        val d = ring.add("d")
        b.moveLeft(2)
        assertEquals(d, b.next)
        assertEquals(b, d.prev)
        assertEquals(b, c.next)
        assertEquals(c, b.prev)
        assertEquals(listOf("a", "c", "b", "d"), a.iterator().toList().map { it.payload })
        assertEquals(listOf("b", "d", "a", "c"), b.iterator().toList().map { it.payload })
    }

    @Test
    fun `if the have many elements, we can insert elements on specific positions from ring element api`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        ring.add("d")
        val x = b.append("x")
        assertEquals(x, b.next)
        assertEquals(b, x.prev)
        assertEquals(c, x.next)
        assertEquals(x, c.prev)
        assertEquals(listOf("a", "b", "x", "c", "d"), a.iterator().toList().map { it.payload })
    }

    @Test
    fun `if the have many elements, we can insert elements on specific positions from ring api`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        ring.add("d")
        val x = ring.insertAfter("x", b)
        assertEquals(x, b.next)
        assertEquals(b, x.prev)
        assertEquals(c, x.next)
        assertEquals(x, c.prev)
        assertEquals(listOf("a", "b", "x", "c", "d"), a.iterator().toList().map { it.payload })
    }

    @Test
    fun `containsAll works as expected`() {
        val ring = Ring<String>()
        ring.add("a")
        ring.add("b")
        ring.add("c")
        assertTrue(ring.containsAll(listOf("a", "b")))
        assertTrue(ring.containsAll(listOf("c")))
        assertFalse(ring.containsAll(listOf("c", "d")))
        assertTrue(ring.containsAll(emptyList()))
    }

    @Test
    fun `ring iterator fails if new element is added concurrently`() {
        val ring = Ring<String>()
        ring.add("a")
        ring.add("b")
        val iterator = ring.iterator()
        assertEquals(true, iterator.hasNext())
        assertEquals("a", iterator.next())
        assertEquals(true, iterator.hasNext())
        ring.add("c")
        assertEquals(true, iterator.hasNext())
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `ring element iterator fails if new element is added concurrently`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        ring.add("b")
        val iterator = a.iterator()
        assertEquals(true, iterator.hasNext())
        assertEquals("a", iterator.next().payload)
        assertEquals(true, iterator.hasNext())
        ring.add("c")
        assertEquals(true, iterator.hasNext())
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `ring iterator fails if element is removed concurrently`() {
        val ring = Ring<String>()
        ring.add("a")
        ring.add("b")
        ring.add("c")
        val iterator = ring.iterator()
        assertEquals(true, iterator.hasNext())
        assertEquals("a", iterator.next())
        assertEquals(true, iterator.hasNext())
        ring.remove("c")
        assertEquals(true, iterator.hasNext())
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `ring element iterator fails if element is removed concurrently`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        ring.add("b")
        ring.add("c")
        val iterator = a.iterator()
        assertEquals(true, iterator.hasNext())
        assertEquals("a", iterator.next().payload)
        assertEquals(true, iterator.hasNext())
        ring.remove("c")
        assertEquals(true, iterator.hasNext())
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `ring iterator fails if element is moved concurrently`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        ring.add("b")
        ring.add("c")
        val iterator = ring.iterator()
        assertEquals(true, iterator.hasNext())
        assertEquals("a", iterator.next())
        assertEquals(true, iterator.hasNext())
        a.moveLeft()
        assertEquals(true, iterator.hasNext())
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `ring element iterator fails if element is moved concurrently`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        ring.add("b")
        ring.add("c")
        val iterator = a.iterator()
        assertEquals(true, iterator.hasNext())
        assertEquals("a", iterator.next().payload)
        assertEquals(true, iterator.hasNext())
        a.moveLeft()
        assertEquals(true, iterator.hasNext())
        assertThrows<ConcurrentModificationException> { iterator.next() }
    }

    @Test
    fun `distance is calculated correctly`() {
        val ring = Ring<String>()
        val a = ring.add("a")
        val b = ring.add("b")
        val c = ring.add("c")
        val d = ring.add("d")
        assertEquals(0, a.distanceTo(a))
        assertEquals(1, a.distanceTo(b))
        assertEquals(-1, b.distanceTo(a))
        assertEquals(-1, a.distanceTo(d))
        assertEquals(2, a.distanceTo(c))
        assertEquals(b, a[a.distanceTo(b)])
    }

    private fun <T> Iterator<T>.toList(): List<T> {
        val result = mutableListOf<T>()
        while(this.hasNext()) {
            result.add(this.next())
        }
        return result
    }
}