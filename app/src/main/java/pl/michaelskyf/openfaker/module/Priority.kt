package pl.michaelskyf.openfaker.module
abstract class Priority(val priority: Int): Comparable<Priority> {
    override fun compareTo(other: Priority): Int {
        return priority.compareTo(other.priority)
    }
}