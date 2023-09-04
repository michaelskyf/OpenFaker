package pl.michaelskyf.openfaker.ui_module_bridge

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface DataTunnel {
    companion object {
        const val fakerDataFileName = "open_faker_module_method_hooks"
    }

    abstract class Receiver {
        private var modifiedKeys: HashSet<String> = hashSetOf()

        operator fun get(className: String, methodName: String): Result<Array<HookHandlerData>> = runCatching {
            val key = "$className.$methodName"
            val json = getString(key)
                ?: throw Exception("Failed to get json from $key")

            modifiedKeys.remove(key)
            Json.decodeFromString(json)
        }

        fun runIfChanged(className: String, methodName: String, callback: Array<HookHandlerData>.() -> Unit) = runCatching {
            val key = "$className.$methodName"
            reload()

            if (modifiedKeys.contains(key)) {
                callback(get(className, methodName).getOrThrow())
                modifiedKeys.remove(key)
            }
        }

        fun all(): Result<List<HookerData>> = runCatching {

            modifiedKeys.clear()
            val rawData = implAll().filterKeys { it != "modifiedKeys" }
            val classHookerDataArray = rawData.map {
                val classMethod = it.key
                val className = classMethod.substringBeforeLast('.')
                val methodName = classMethod.substringAfterLast('.')

                val dataArray = Json.decodeFromString<Array<HookHandlerData>>(it.value)

                HookerData(className, methodName, dataArray)
            }

            classHookerDataArray
        }

        fun reload(): Boolean {
            if (!implReload()) return false

            val json = getString("modifiedKeys") ?: return false
            val newModifiedKeys = Json.decodeFromString<Array<String>>(json)

            modifiedKeys.addAll(newModifiedKeys)

            return true
        }

        protected abstract fun implReload(): Boolean
        protected abstract fun getString(key: String): String?
        protected abstract fun implAll(): Map<String, String>
    }

    interface Sender {
        operator fun set(className: String, methodName: String, hookHandlerData: Array<HookHandlerData>): Result<Unit> = runCatching {

            edit().putMethodData(HookerData(className, methodName, hookHandlerData)).getOrThrow().commit()
        }

        fun edit(action: Editor.() -> Unit)
        fun edit(): Editor
        abstract class Editor {
            private val modifiedKeys = mutableSetOf<String>()

            fun putMethodData(hookerData: HookerData): Result<Editor> = runCatching {
                val key = "${hookerData.className}.${hookerData.methodName}"

                val json = Json.encodeToString(hookerData.hookData)

                modifiedKeys.add(key)
                implPutString(key, json)

                this
            }

            fun commit(): Boolean {
                val json = Json.encodeToString(modifiedKeys.toTypedArray())
                implPutString("modifiedKeys", json)

                return implCommit()
            }

            protected abstract fun implPutString(key: String, value: String)
            protected abstract fun implCommit(): Boolean
        }
    }
}