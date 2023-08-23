package pl.michaelskyf.openfaker.module.lua

import org.luaj.vm2.LuaFunction
import pl.michaelskyf.openfaker.module.lua.FunctionArgument

class TemporaryRegistry {

    val exactMatchArguments = mutableListOf<Array<out FunctionArgument>>()
    val customArgumentMatchingFunctions = mutableListOf<LuaFunction>()
    fun exactMatchArguments(vararg arguments: FunctionArgument) {
        exactMatchArguments.add(arguments)
    }

    fun customMatchArgument(luaFunction: LuaFunction) {
        customArgumentMatchingFunctions.add(luaFunction)
    }
}