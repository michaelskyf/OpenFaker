package pl.michaelskyf.openfaker.lua

import pl.michaelskyf.openfaker.module.Logger
import pl.michaelskyf.openfaker.module.Priority
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder

class LuaScriptHolder(
    val className: String,
    val methodName: String,
    val argumentTypes: Array<String>,
    val luaScript: String,
    val priority: Int,
    val whenToHook: MethodHookHolder.WhenToHook
    ) {
    fun toMethodHookHolder(logger: Logger): Result<MethodHookHolder> = kotlin.runCatching {
        MethodHookHolder(className, methodName, argumentTypes, LuaFakerModule(priority, luaScript, logger).getOrThrow(), whenToHook)
    }
}