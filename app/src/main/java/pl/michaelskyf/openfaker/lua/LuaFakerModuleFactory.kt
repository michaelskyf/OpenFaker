package pl.michaelskyf.openfaker.lua

import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.Logger
import pl.michaelskyf.openfaker.ui_module_bridge.FakerModuleFactory

class LuaFakerModuleFactory(private val luaScript: String, private val priority: Int): FakerModuleFactory {
    override fun createFakerModule(logger: Logger): Result<FakerModule> = runCatching {
        LuaFakerModule(priority, luaScript, logger).getOrThrow()
    }
}