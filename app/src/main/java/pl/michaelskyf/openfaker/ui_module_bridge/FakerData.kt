package pl.michaelskyf.openfaker.ui_module_bridge

import pl.michaelskyf.openfaker.lua.LuaScriptHolder

abstract class FakerData {
    companion object {
        const val fakerDataFileName = "open_faker_module_method_hooks"
    }

    abstract operator fun get(className: String, methodName: String): Result<Array<MethodHookHolder>>
    abstract operator fun set(className: String, methodName: String, json: String)
    abstract fun all(): Set<Array<LuaScriptHolder>>
    abstract fun reload(): Boolean
}