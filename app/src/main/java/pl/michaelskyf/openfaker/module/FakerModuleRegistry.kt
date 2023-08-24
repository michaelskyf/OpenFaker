package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.module.lua.ArgumentMatcher
import java.util.PriorityQueue

class FakerModuleRegistry {

    private val argumentMatcher = ArgumentMatcher()
    private val argumentMatchingFunctions = PriorityQueue<FakerArgumentCheckerFunction>()
    fun register(module: FakerModule) {

        val matchingArgumentsInfo = module.getMatchingArgumentsInfo()
        matchingArgumentsInfo.exactMatchArguments.forEach {
            argumentMatcher.add(it, module)
        }

        argumentMatchingFunctions.addAll(matchingArgumentsInfo.customArgumentMatchingFunctions)
    }

    fun forEachMatchingModule(functionArguments: Array<Any?>, block: FakerModule.() -> Unit) {
        val matchingArguments = argumentMatcher.match(functionArguments)
        val mergedPriorityQueues = matchingArguments + argumentMatchingFunctions

        for (element in mergedPriorityQueues) {

            val module = when(element) {
                is FakerArgumentCheckerFunction -> element.call(*functionArguments).getOrNull() ?: continue
                else -> element as FakerModule
            }

            block(module)
        }
    }
}