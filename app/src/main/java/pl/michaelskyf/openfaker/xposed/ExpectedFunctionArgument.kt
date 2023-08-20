package pl.michaelskyf.openfaker.xposed

class ExpectedFunctionArgument<T> private constructor(private val classType: Class<Any>, val expectedArgument: T?, val compareOperation: CompareOperation) {

    companion object {
        inline operator fun <reified T> invoke(expectedArgument: T?, compareOperation: CompareOperation = CompareOperation.Equal)
            = ExpectedFunctionArgument(T::class.java, expectedArgument, compareOperation)

        operator fun <T> invoke(classType: Class<T>, expectedArgument: T?, compareOperation: CompareOperation = CompareOperation.Equal)
                = ExpectedFunctionArgument(classType as Class<Any>, expectedArgument, compareOperation)
    }
    enum class CompareOperation {
        Equal,
        Unequal,
        LessThanExpected,
        AlwaysTrue
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
        = (other is ExpectedFunctionArgument<*>)
            && this.classType == other.classType
            && this.expectedArgument == other.expectedArgument
            && this.compareOperation == other.compareOperation
}