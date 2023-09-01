package pl.michaelskyf.openfaker.lua

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform
import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.FunctionArgument
import pl.michaelskyf.openfaker.module.Logger
import pl.michaelskyf.openfaker.module.MatchingArgumentsInfo
import pl.michaelskyf.openfaker.module.MethodHookParameters
import java.util.Optional

class LuaFakerModule private constructor(
    priority: Int,
    private val runModule: LuaFunction,
    private val registerModule: LuaFunction
) : FakerModule(priority) {

    companion object {
        operator fun invoke(priority: Int, luaSource: String, logger: Logger): Result<FakerModule>
            = runCatching {
                val globals = JsePlatform.standardGlobals()
                globals.load(luaSource).call()

                globals.set("argument", CoerceJavaToLua.coerce(FunctionArgument::class.java))
                globals.set("logger", CoerceJavaToLua.coerce(logger))
                val registerModule = globals.get("registerModule").checkfunction()
                val runModule = globals.get("runModule").checkfunction()

                LuaFakerModule(priority, runModule, registerModule)
            }
    }

    override fun run(hookParameters: MethodHookParameters): Result<Boolean>
        = runCatching { runModule.call(CoerceJavaToLua.coerce(hookParameters)).checkboolean() }

    override fun getMatchingArgumentsInfo(): Result<MatchingArgumentsInfo>
        = runCatching {
            val matchingArgumentsInfo = LuaMatchingArgumentsInfo()
            registerModule.call(CoerceJavaToLua.coerce(matchingArgumentsInfo))
            matchingArgumentsInfo
        }

    inner class LuaFakerArgumentCheckerFunction(private val luaFunction: LuaFunction) : FakerArgumentCheckerFunction() {
        override fun call(vararg arguments: Any?): Result<Optional<FakerModule>>
            = runCatching {
                val result = luaFunction.call(CoerceJavaToLua.coerce(arguments))
                when (result.checkboolean()) {
                    true -> Optional.of(this@LuaFakerModule)
                    false -> Optional.empty()
                }
            }
    }

    inner class LuaMatchingArgumentsInfo: MatchingArgumentsInfo() {
        fun exactMatchArguments(vararg arguments: FunctionArgument) {
            exactMatchArguments.add(arguments)
        }

        fun customMatchArgument(luaFunction: LuaFunction) {
            customArgumentMatchingFunctions.add(LuaFakerArgumentCheckerFunction(luaFunction))
        }
    }
}