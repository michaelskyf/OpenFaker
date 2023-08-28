package pl.michaelskyf.openfaker.ui_module_bridge

import pl.michaelskyf.openfaker.lua.LuaScriptHolder

abstract class FakerData {
    companion object {
        const val fakerDataFileName = "faker_data_shared_preferences"
    }

    val methodHooksKey = "methodHooks"
    abstract var methodHooks: Array<LuaScriptHolder>
    abstract fun hasChanged(): Boolean
}