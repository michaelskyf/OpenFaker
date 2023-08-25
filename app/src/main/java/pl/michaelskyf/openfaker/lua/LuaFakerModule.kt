package pl.michaelskyf.openfaker.lua

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform
import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.FunctionArgument
import pl.michaelskyf.openfaker.module.MatchingArgumentsInfo
import pl.michaelskyf.openfaker.module.MethodHookParameters
import java.util.Optional

class LuaFakerModule private constructor(
    priority: Int,
    private val globals: Globals,
    private val runModule: LuaFunction,
    private val registerModule: LuaFunction
) : FakerModule(priority) {

    companion object {
        operator fun invoke(priority: Int, luaSource: String): Result<FakerModule> {
            return try {
                val globals = JsePlatform.standardGlobals()
                globals.load(luaSource).call()

                globals.set("argument", CoerceJavaToLua.coerce(FunctionArgument::class.java))
                val registerModule = globals.get("registerModule").checkfunction()
                val runModule = globals.get("runModule").checkfunction()

                Result.success(LuaFakerModule(priority, globals, runModule, registerModule))
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }
    }

    override fun run(hookParameters: MethodHookParameters): Result<Boolean> {
        return try {
            val result = runModule.call(CoerceJavaToLua.coerce(hookParameters))
            return Result.success(result.checkboolean())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override fun getMatchingArgumentsInfo(): Result<MatchingArgumentsInfo> {
        val matchingArgumentsInfo = LuaMatchingArgumentsInfo()
        return try {
            registerModule.call(CoerceJavaToLua.coerce(matchingArgumentsInfo))
            Result.success(matchingArgumentsInfo)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    inner class LuaFakerArgumentCheckerFunction(private val luaFunction: LuaFunction) : FakerArgumentCheckerFunction() {
        override fun call(vararg arguments: Any?): Result<Optional<FakerModule>> {
            return try {
                val result = luaFunction.call(CoerceJavaToLua.coerce(arguments))
                Result.success(when (result.checkboolean()) {
                    true -> Optional.of(this@LuaFakerModule)
                    false -> Optional.empty()
                })
            } catch (exception: Exception) {
                Result.failure(exception)
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