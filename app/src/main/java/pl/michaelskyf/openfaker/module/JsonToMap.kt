package pl.michaelskyf.openfaker.module

import com.google.gson.Gson
import pl.michaelskyf.openfaker.xposed.ClassMethodPair
import pl.michaelskyf.openfaker.xposed.MethodFakeValueArgsPair


class JsonToMap {

    fun getMapFromJson(json: String): Map<ClassMethodPair, MethodFakeValueArgsPair>? {

        val map = mutableMapOf<ClassMethodPair, MethodFakeValueArgsPair>()

        val argumentArray = Gson().fromJson(json, Array<MethodArguments>::class.java)
            ?: return null

        for (arg in argumentArray) {

            try {
                val mappedArray = arg.typeValuePairArray.map {
                    val foundClass = Class.forName(it.first) ?: throw ClassNotFoundException()
                    var expectedValue = it.second

                    if (expectedValue != null && expectedValue.javaClass == java.lang.Double::class.java && foundClass == Integer::class.java) {
                        val double = expectedValue as Double
                        expectedValue = double.toInt()
                    }

                    ExpectedFunctionArgument(foundClass, expectedValue, it.third)
                }.toTypedArray()

                map[Pair(arg.className, arg.methodName)] = Pair(arg.fakeValue, mappedArray)
            } catch (exception: ClassNotFoundException) {
                return null
            }
        }

        return map
    }
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
}