package pl.michaelskyf.openfaker.module

data class MatchingArgumentsInfo(
    val exactMatchArguments: MutableList<Array<out FunctionArgument>>,
    val customArgumentMatchingFunctions: MutableList<FakerModule.FakerArgumentCheckerFunction>
    )