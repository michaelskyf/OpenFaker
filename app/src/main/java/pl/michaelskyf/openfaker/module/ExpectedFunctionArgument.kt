package pl.michaelskyf.openfaker.module

import java.lang.Exception

class ExpectedFunctionArgument (val classType: Class<*>, val expectedArgument: Any?, val compareOperation: CompareOperation = CompareOperation.Equal) {

    enum class CompareOperation {
        Equal,
        Unequal,
        LessThanExpected,
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

    override fun equals(other: Any?)
        = (other is ExpectedFunctionArgument)
            && this.classType == other.classType
            && this.expectedArgument == other.expectedArgument
            && this.compareOperation == other.compareOperation

    override fun hashCode(): Int {
        var result = classType.hashCode()
        result = 31 * result + (expectedArgument?.hashCode() ?: 0)
        result = 31 * result + compareOperation.hashCode()
        return result
    }
}