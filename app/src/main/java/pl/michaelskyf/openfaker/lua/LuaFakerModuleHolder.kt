package pl.michaelskyf.openfaker.lua

import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.Logger
import pl.michaelskyf.openfaker.ui_module_bridge.FakerModuleHolder

class LuaFakerModuleHolder(val luaScript: String, priority: Int): FakerModuleHolder(priority) {
    override fun toFakerModule(logger: Logger): Result<FakerModule> = runCatching {
        LuaFakerModule(priority, luaScript, logger).getOrThrow()
    }
}