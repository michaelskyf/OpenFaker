package pl.michaelskyf.openfaker.module

import java.lang.Exception

abstract class FakerModule(val priority: Int): Comparable<FakerModule> {

    abstract fun run(hookParameters: MethodHookParameters): Result<Void>
    abstract fun getMatchingArgumentsInfo()
}