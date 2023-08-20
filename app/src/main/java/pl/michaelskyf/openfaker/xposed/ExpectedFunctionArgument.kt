package pl.michaelskyf.openfaker.xposed

class ExpectedFunctionArgument<T>(val classType: Class<T>, val expectedArgument: T?, val compareOperation: CompareOperation) {

    companion object {
        inline operator fun <reified T> invoke(expectedArgument: T?, compareOperation: CompareOperation = CompareOperation.Equal)
            = ExpectedFunctionArgument(T::class.java, expectedArgument, compareOperation)

        operator fun invoke(classType: Class<Any>, expectedArgument: Any?, compareOperation: CompareOperation = CompareOperation.Equal)
                = ExpectedFunctionArgument(classType, expectedArgument, compareOperation)
    }
    enum class CompareOperation {
        Equal,
        NotEqual,
        LessThanExpected,
        AlwaysTrue
    }
    inline fun <reified AT> matches(functionArgument: AT?): Boolean {

        return when (compareOperation) {
            CompareOperation.Equal -> functionArgument == expectedArgument
            CompareOperation.NotEqual -> functionArgument != expectedArgument
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

    fun getType(): Class<T> {
        return classType
    }
}