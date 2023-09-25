package pl.michaelskyf.openfaker.ui_module_bridge

import kotlinx.serialization.Serializable
import pl.michaelskyf.openfaker.lua.LuaFakerModule
import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.Logger

@Serializable
class LuaFakerModuleFactory(private val luaScript: String, private val userData: Array<String>?, private val priority: Int): FakerModuleFactory {
    override fun createFakerModule(logger: Logger): Result<FakerModule>
        = LuaFakerModule(luaScript, userData, priority, logger)
}