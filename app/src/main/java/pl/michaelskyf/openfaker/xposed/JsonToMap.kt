package pl.michaelskyf.openfaker.xposed

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import de.robv.android.xposed.XSharedPreferences
import pl.michaelskyf.openfaker.BuildConfig
import java.lang.reflect.Type


class JsonToMap {

    fun getMapFromJson(json: String): Map<ClassMethodPair, MethodFakeValueArgsPair>? {

        val map = mutableMapOf<ClassMethodPair, MethodFakeValueArgsPair>()

        val argumentArray = Gson().fromJson(json, Array<MethodArguments>::class.java)
            ?: return null

        for (arg in argumentArray) {

            try {
                val convertedArray =
                    arg.typeValuePairArray.map { ExpectedFunctionArgument(Class.forName(it.first) as Class<Any>, it.second) }
                        .toTypedArray()
                map[Pair(arg.className, arg.methodName)] = Pair(arg.fakeValue, convertedArray)
            } catch (exception: ClassNotFoundException) {
                return null
            }
        }

        return map
    }
    data class MethodArguments(

        val className: String,
        val methodName: String,
        val fakeValue: Any,
        val typeValuePairArray: Array<Pair<String, Any?>>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MethodArguments

            if (className != other.className) return false
            if (methodName != other.methodName) return false
            if (fakeValue != other.fakeValue) return false
            if (!typeValuePairArray.contentEquals(other.typeValuePairArray)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = className.hashCode()
            result = 31 * result + methodName.hashCode()
            result = 31 * result + fakeValue.hashCode()
            result = 31 * result + typeValuePairArray.contentHashCode()
            return result
        }
    }
}