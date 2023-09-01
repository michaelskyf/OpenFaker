package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.FunctionArgument

data class MatchingArgumentsInfo(
    val exactMatchArguments: MutableList<Array<out FunctionArgument>>,
    val customArgumentMatchingFunctions: MutableList<FakerModule.FakerArgumentCheckerFunction>
    )