package pl.michaelskyf.openfaker.module.lua

import org.luaj.vm2.LuaFunction
import pl.michaelskyf.openfaker.module.FakerArgumentCheckerFunction

class MatchingArgumentsInfo {

    val exactMatchArguments = mutableListOf<Array<out FunctionArgument>>()
    val customArgumentMatchingFunctions = mutableListOf<FakerArgumentCheckerFunction>()
    fun exactMatchArguments(vararg arguments: FunctionArgument) {
        exactMatchArguments.add(arguments)
    }

    fun customMatchArgument(function: FakerArgumentCheckerFunction) {
        customArgumentMatchingFunctions.add(function)
    }
}