package pl.michaelskyf.openfaker.ui_module_bridge

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type


class FakerData {
    companion object {
        const val fakerDataFileName = "open_faker_module_method_hooks"
    }

    abstract class Receiver {
        operator fun get(className: String, methodName: String): Result<Array<MethodHookHolder>> = runCatching {
            val json = getString("$className.$methodName")
                ?: throw Exception("Failed to get json from $className.$methodName")

            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(FakerModuleHolder::class.java, PropertyBasedInterfaceMarshal())
            val gson = gsonBuilder.create()
            gson.fromJson(json, Array<MethodHookHolder>::class.java)
        }

        fun runIfChanged(className: String, methodName: String, callback: (Array<MethodHookHolder>) -> Unit) {
            TODO("runIfChanged")
        }

        fun all(): Result<Set<Array<MethodHookHolder>>> = runCatching {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(FakerModuleHolder::class.java, PropertyBasedInterfaceMarshal())
            val gson = gsonBuilder.create()

            getAll().map { gson.fromJson(it.value, Array<MethodHookHolder>::class.java) }.toSet()
        }


        abstract fun reload(): Boolean
        protected abstract fun getString(key: String): String?
        protected abstract fun getAll(): Map<String, String>
    }

    abstract class Sender {
        operator fun set(className: String, methodName: String, methodHookHolders: Array<MethodHookHolder>): Result<Unit> = runCatching {

            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(FakerModuleHolder::class.java, PropertyBasedInterfaceMarshal())
            val gson = gsonBuilder.create()

            val json = gson.toJson(methodHookHolders)

            setString("$className.$methodName", json)
        }

        protected abstract fun setString(key: String, value: String?): Boolean
    }

    // https://stackoverflow.com/questions/3629596/deserializing-an-abstract-class-in-gson Thank you very much! :)
    private class PropertyBasedInterfaceMarshal : JsonSerializer<Any?>,
        JsonDeserializer<Any?> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            jsonElement: JsonElement, type: Type?,
            jsonDeserializationContext: JsonDeserializationContext
        ): Any {
            val jsonObj = jsonElement.asJsonObject
            val className = jsonObj[CLASS_META_KEY].asString
            return try {
                val clz = Class.forName(className)
                jsonDeserializationContext.deserialize<Any>(jsonElement, clz)
            } catch (e: ClassNotFoundException) {
                throw JsonParseException(e)
            }
        }

        override fun serialize(
            src: Any?,
            type: Type?,
            jsonSerializationContext: JsonSerializationContext?
        ): JsonElement? {
            val jsonEle = jsonSerializationContext!!.serialize(src, src!!.javaClass)
            jsonEle.asJsonObject.addProperty(
                CLASS_META_KEY,
                src.javaClass.canonicalName
            )
            return jsonEle
        }

        companion object {
            private const val CLASS_META_KEY = "CLASS_META_KEY"
        }
    }
}