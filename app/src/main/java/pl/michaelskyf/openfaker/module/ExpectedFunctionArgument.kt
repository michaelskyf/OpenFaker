package pl.michaelskyf.openfaker.module

import java.lang.Exception

class ExpectedFunctionArgument (val classType: Class<*>, val expectedArgument: Any?, val compareOperation: CompareOperation = CompareOperation.Equal) {

    enum class CompareOperation {
        Equal,
        Unequal,
        LessThanExpected,
        LessEqualExpected,
        GreaterThanExpected,
        GreaterEqualExpected,
        AlwaysTrue
    }

    init {
        if (compareOperation != CompareOperation.AlwaysTrue && expectedArgument != null && classType != expectedArgument.javaClass) {
            throw Exception("Types $classType and " + expectedArgument.javaClass + " do not match") // TODO: Custom exception?
        }
    }

    companion object {
        inline operator fun <reified T> invoke(expectedArgument: T?, compareOperation: CompareOperation = CompareOperation.Equal)
            = ExpectedFunctionArgument(T::class.java, expectedArgument, compareOperation)
    }
    inline fun <reified AT> matches(functionArgument: AT?): Boolean {

        return when (compareOperation) {
            CompareOperation.Equal -> functionArgument == expectedArgument
            CompareOperation.Unequal -> functionArgument != expectedArgument
            CompareOperation.LessThanExpected -> compareTo(functionArgument)?.let { it < 0 } ?: false
            CompareOperation.LessEqualExpected -> compareTo(functionArgument)?.let { it <= 0 } ?: false
            CompareOperation.GreaterThanExpected -> compareTo(functionArgument)?.let { it > 0 } ?: false
            CompareOperation.GreaterEqualExpected -> compareTo(functionArgument)?.let { it >= 0 } ?: false
            CompareOperation.AlwaysTrue -> true
        }
    }

    inline fun <reified AT> compareTo(functionArgument: AT?): Int? {

        tryCast<Comparable<AT>>(functionArgument) {

            val lh = this

            tryCast<AT>(expectedArgument) {
                return lh.compareTo(this)
            }
        }

        return null
    }

    inline fun <reified T> tryCast(instance: Any?, block: T.() -> Unit) {
        if (instance is T) {
            block(instance)
        }
    }

    fun getType(): Class<*> {
        return classType
    }

    override fun hashCode(): Int {
        var result = classType.hashCode()
        result = 31 * result + (expectedArgument?.hashCode() ?: 0)
        result = 31 * result + compareOperation.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpectedFunctionArgument

        if (classType != other.classType) return false
        if (expectedArgument != other.expectedArgument) return false
        if (compareOperation != other.compareOperation) return false

        return true
    }
}