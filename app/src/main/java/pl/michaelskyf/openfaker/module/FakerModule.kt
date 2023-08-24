package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.module.lua.FunctionArgument
import pl.michaelskyf.openfaker.module.lua.MatchingArgumentsInfo
import java.lang.Exception

abstract class FakerModule(val priority: Int): Comparable<FakerModule> {

    abstract fun run(hookParameters: MethodHookParameters): Result<Boolean>
    abstract fun getMatchingArgumentsInfo(): MatchingArgumentsInfo
}