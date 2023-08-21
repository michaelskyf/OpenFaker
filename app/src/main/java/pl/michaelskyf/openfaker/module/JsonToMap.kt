package pl.michaelskyf.openfaker.module

import com.google.gson.Gson
import pl.michaelskyf.openfaker.xposed.ClassMethodPair
import pl.michaelskyf.openfaker.xposed.MethodFakeValueArgsPair


class JsonToMap {

    fun getMapFromJson(json: String): Map<ClassMethodPair, MethodFakeValueArgsPair>? {

        val argumentArray = Gson().fromJson(json, Array<MethodArguments>::class.java)
            ?: return null

        val map = mutableMapOf<ClassMethodPair, MethodFakeValueArgsPair>()

        for (arg in argumentArray) {

            val mappedArray = arg.typeValuePairArray.map {
                val foundClass = getClassForName(it.first) ?: return null

                val expectedValue = convertDoubleToIntIfTheTypeIsCorrect(it.second, foundClass)

                ExpectedFunctionArgument(foundClass, expectedValue, it.third)
            }.toTypedArray()

            map[Pair(arg.className, arg.methodName)] = Pair(arg.fakeValue, mappedArray)
        }

        return map
    }

    // This function exists because Gson by default converts integers to doubles
    private fun convertDoubleToIntIfTheTypeIsCorrect(value: Any?, clazz: Class<*>): Any? {

        if (value == null) return null

        if (value.javaClass == java.lang.Double::class.java && clazz == Integer::class.java) {
            return (value as Double).toInt()
        }

        return value
    }

    private fun getClassForName(className: String): Class<*>? {
        return try {
            Class.forName(className)
        } catch (exception: Exception) {
            null
        }
    }
}