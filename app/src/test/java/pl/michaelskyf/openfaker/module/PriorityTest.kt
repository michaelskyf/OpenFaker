package pl.michaelskyf.openfaker.module

import org.junit.jupiter.api.Test

class PriorityTest {

    @Test
    fun `greater operator should return true when its priority value is greater than the other priority`() {
        val priority = Priority(0)
        val smallerPriority = Priority(-10)

        assert(priority > smallerPriority)
    }

    @Test
    fun `equality operator should return true when its priority value is equal to the other priority`() {
        val priority = Priority(0)
        val equalPriority = Priority(0)

        assert(priority == equalPriority)
    }

    @Test
    fun `smaller operator should return true when its priority value is smaller than the other priority`() {
        val priority = Priority(0)
        val greaterPriority = Priority(10)

        assert(priority < greaterPriority)
    }

    @Test
    fun `hashCode() of two distinct priorities should not be equal`() {
        val priority = Priority(0)
        val otherPriority = Priority(10)

        assert(priority.hashCode() != otherPriority.hashCode())
    }

    @Test
    fun `hashCode() of two equal priorities should be equal`() {
        val priority = Priority(0)
        val otherPriority = Priority(0)

        assert(priority == otherPriority)
        assert(priority.hashCode() == otherPriority.hashCode())
    }
}