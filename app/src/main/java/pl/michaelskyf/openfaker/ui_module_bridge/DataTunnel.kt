package pl.michaelskyf.openfaker.ui_module_bridge

import com.google.gson.Gson
import com.google.gson.GsonBuilder

interface DataTunnel {
    companion object {
        const val fakerDataFileName = "open_faker_module_method_hooks"
        private fun getGson(): Result<Gson> = runCatching {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(FakerModuleFactory::class.java, PropertyBasedInterfaceMarshal())
            gsonBuilder.registerTypeAdapterFactory(SealedTypeAdapterFactory.of(HookData.WhichPackages::class))

            gsonBuilder.create()
        }
    }

    abstract class Receiver {

        private var modifiedKeys: HashSet<String> = hashSetOf()

        operator fun get(className: String, methodName: String): Result<Array<HookData>> = runCatching {
            val key = "$className.$methodName"
            val json = getString(key)
                ?: throw Exception("Failed to get json from $key")

            val gson = getGson().getOrThrow()

            modifiedKeys.remove(key)
            gson.fromJson(json, Array<HookData>::class.java)
        }

        fun runIfChanged(className: String, methodName: String, callback: Array<HookData>.() -> Unit) = runCatching {
            val key = "$className.$methodName"
            reload()

            if (modifiedKeys.contains(key)) {
                callback(get(className, methodName).getOrThrow())
                modifiedKeys.remove(key)
            }
        }

        fun all(): Result<List<MethodData>> = runCatching {
            val gson = getGson().getOrThrow()

            modifiedKeys.clear()
            val rawData = implAll().filterKeys { it != "modifiedKeys" }
            val classMethodDataArray = rawData.map {
                val classMethod = it.key
                val className = classMethod.substringBeforeLast('.')
                val methodName = classMethod.substringAfterLast('.')

                val dataArray = gson.fromJson(it.value, Array<HookData>::class.java)

                MethodData(className, methodName, dataArray)
            }

            classMethodDataArray
        }

        fun reload(): Boolean {
            if (!implReload()) return false

            val json = getString("modifiedKeys") ?: return false
            val newModifiedKeys = Gson().fromJson(json, Array<String>::class.java)

            modifiedKeys.addAll(newModifiedKeys)

            return true
        }

        protected abstract fun implReload(): Boolean
        protected abstract fun getString(key: String): String?
        protected abstract fun implAll(): Map<String, String>
    }

    interface Sender {
        operator fun set(className: String, methodName: String, hookData: Array<HookData>): Result<Unit> = runCatching {

            edit().putMethodData(MethodData(className, methodName, hookData)).getOrThrow().commit()
        }

        fun edit(action: Editor.() -> Unit)
        fun edit(): Editor
        abstract class Editor {
            private val modifiedKeys = mutableSetOf<String>()

            fun putMethodData(methodData: MethodData): Result<Editor> = runCatching {
                val key = "${methodData.className}.${methodData.methodName}"

                val gson = getGson().getOrThrow()

                val json = gson.toJson(methodData.hookData)

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
}