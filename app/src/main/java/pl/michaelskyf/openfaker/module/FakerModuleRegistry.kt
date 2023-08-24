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

    fun getMatchingModules(hookedFunctionArguments: Array<*>): MatchingModulesIterator {
        val matchingArguments = argumentMatcher.match(hookedFunctionArguments)
        val mergedPriorityQueues = matchingArguments + argumentMatchingFunctions

        return MatchingModulesIterator(hookedFunctionArguments, mergedPriorityQueues)
    }
    inner class MatchingModulesIterator(val hookedFunctionArguments: Array<*>, val mergedMatchers: List<Any>) : Iterator<FakerModule> {

        private val queueIterator = mergedMatchers.iterator()
        private lateinit var nextModule: FakerModule
        override fun hasNext(): Boolean {

            while (queueIterator.hasNext()) {
                val element = queueIterator.next()

                nextModule = when(element) {
                    is FakerArgumentCheckerFunction -> element.call(*hookedFunctionArguments).getOrNull() ?: continue
                    else -> element as FakerModule
                }

                return true
            }

            return false
        }

        override fun next(): FakerModule {
            return nextModule
        }
    }
}