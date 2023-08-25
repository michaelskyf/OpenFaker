package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.FunctionArgument

abstract class MatchingArgumentsInfo {

    val exactMatchArguments = mutableListOf<Array<out FunctionArgument>>()
    val customArgumentMatchingFunctions = mutableListOf<FakerModule.FakerArgumentCheckerFunction>()
}