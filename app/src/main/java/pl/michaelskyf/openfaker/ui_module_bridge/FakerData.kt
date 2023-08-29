package pl.michaelskyf.openfaker.ui_module_bridge

import com.google.gson.Gson

class FakerData {
    companion object {
        const val fakerDataFileName = "open_faker_module_method_hooks"
    }

    abstract class Receiver {
        operator fun get(className: String, methodName: String): Result<Array<MethodHookHolder>> = runCatching {
            val json = getString("$className.$methodName")
                ?: throw Exception("Failed to get json from $className.$methodName")

            Gson().fromJson(json, Array<MethodHookHolder>::class.java)
        }

        fun runIfChanged(className: String, methodName: String, callback: (Array<MethodHookHolder>) -> Unit) {
            TODO()
        }

        abstract fun all(): Set<Array<MethodHookHolder>>
        abstract fun reload(): Boolean
        protected abstract fun getString(key: String): String?
    }

    abstract class Sender {
        operator fun set(className: String, methodName: String, json: String): Boolean
            = setString("$className.$methodName", json)

        protected abstract fun setString(key: String, value: String?): Boolean
    }
}