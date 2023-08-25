package pl.michaelskyf.openfaker.module.lua

import pl.michaelskyf.openfaker.module.FakerModule

class MatchingArgumentsInfo {

    val exactMatchArguments = mutableListOf<Array<out FunctionArgument>>()
    val customArgumentMatchingFunctions = mutableListOf<FakerModule.FakerArgumentCheckerFunction>()
    fun exactMatchArguments(vararg arguments: FunctionArgument) {
        exactMatchArguments.add(arguments)
    }

    fun customMatchArgument(function: FakerModule.FakerArgumentCheckerFunction) {
        customArgumentMatchingFunctions.add(function)
    }
}