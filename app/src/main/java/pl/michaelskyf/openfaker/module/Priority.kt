package pl.michaelskyf.openfaker.module
open class Priority(val priority: Int): Comparable<Priority> {
    override fun compareTo(other: Priority): Int {
        return priority.compareTo(other.priority)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Priority) return false

        return priority == other.priority
    }

    override fun hashCode(): Int {
        return priority
    }
}