package pl.michaelskyf.openfaker.module

class MethodArguments(

    val className: String,
    val methodName: String,
    val fakeValue: Any,
    val typeValuePairArray: Array<Triple<String, Any?, ExpectedFunctionArgument.CompareOperation>>
) {
    companion object {
        operator fun invoke(className: String,
                            methodName: String,
                            fakeValue: Any,
                            typeValuePairArray: Array<ExpectedFunctionArgument>
        ): MethodArguments {

            val mappedArguments = typeValuePairArray.map { Triple(it.getType().typeName, it.expectedArgument, it.compareOperation) }.toTypedArray()
            return MethodArguments(className, methodName, fakeValue, mappedArguments)
        }
    }
}