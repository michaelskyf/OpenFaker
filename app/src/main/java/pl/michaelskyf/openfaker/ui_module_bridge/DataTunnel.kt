package pl.michaelskyf.openfaker.ui_module_bridge

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type


class DataTunnel {
    companion object {
        const val fakerDataFileName = "open_faker_module_method_hooks"
    }

    abstract class Receiver {

        private var modifiedKeys: HashSet<String> = hashSetOf()

        operator fun get(className: String, methodName: String): Result<Array<HookData>> = runCatching {
            val key = "$className.$methodName"
            val json = getString(key)
                ?: throw Exception("Failed to get json from $key")

            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(FakerModuleFactory::class.java, PropertyBasedInterfaceMarshal())
            val gson = gsonBuilder.create()

            modifiedKeys.remove(key)
            gson.fromJson(json, Array<HookData>::class.java)
        }

        fun runIfChanged(className: String, methodName: String, callback: Array<HookData>.() -> Unit) = runCatching {
            reload()

            val key = "$className.$methodName"

            if (modifiedKeys.contains(key)) {
                callback(get(className, methodName).getOrThrow())
                modifiedKeys.remove(key)
            }
        }

        fun all(): Result<Collection<MethodData>> = runCatching {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(FakerModuleFactory::class.java, PropertyBasedInterfaceMarshal())
            val gson = gsonBuilder.create()

            modifiedKeys.clear()
            val rawData = getAll().filterKeys { it != "modifiedKeys" } // .map { gson.fromJson(it.value, Array<HookData>::class.java) }
            val classMethodDataArray = rawData.map {
                val classMethod = it.key
                val className = classMethod.substringBeforeLast('.')
                val methodName = classMethod.substringAfterLast('.')

                val dataArray = gson.fromJson(it.value, Array<HookData>::class.java)

                MethodData(className, methodName, dataArray)
            }

            classMethodDataArray
        }

        fun reload() {
            if (!implReload()) return

            val json = getString("modifiedKeys") ?: return
            val newModifiedKeys = Gson().fromJson(json, Array<String>::class.java)

            modifiedKeys.addAll(newModifiedKeys)
        }

        abstract fun implReload(): Boolean
        protected abstract fun getString(key: String): String?
        protected abstract fun getAll(): Map<String, String>
    }

    interface Sender {
        operator fun set(className: String, methodName: String, hookData: Array<HookData>): Result<Unit> = runCatching {

            edit().putMethodHookHolders(className, methodName, hookData).getOrThrow().commit()
        }

        fun edit(action: Editor.() -> Unit)
        fun edit(): Editor
        abstract class Editor {
            private val modifiedKeys = mutableSetOf<String>()

            fun putMethodHookHolders(className: String, methodName: String, hookData: Array<HookData>): Result<Editor> = runCatching {
                val key = "$className.$methodName"

                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(FakerModuleFactory::class.java, PropertyBasedInterfaceMarshal())
                val gson = gsonBuilder.create()

                val json = gson.toJson(hookData)

                modifiedKeys.add(key)
                implPutString(key, json)

                this
            }

            fun commit(): Boolean {
                val json = Gson().toJson(modifiedKeys.toTypedArray())
                implPutString("modifiedKeys", json)

                return implCommit()
            }

            protected abstract fun implPutString(key: String, value: String)
            protected abstract fun implCommit(): Boolean
        }
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