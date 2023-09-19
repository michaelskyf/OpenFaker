package pl.michaelskyf.openfaker.module

import java.util.PriorityQueue
import kotlin.jvm.optionals.getOrNull

class FakerModuleRegistry {

    private val argumentMatcher = ArgumentMatcher()
    private val argumentMatchingFunctions = mutableListOf<FakerModule.FakerArgumentCheckerFunction>()
    fun register(module: FakerModule): Result<Unit> {

        val matchingArgumentsInfo = module.getMatchingArgumentsInfo().getOrElse { return Result.failure(it) }
        matchingArgumentsInfo.exactMatchArguments.forEach {
            argumentMatcher.add(it, module)
        }

        argumentMatchingFunctions.addAll(matchingArgumentsInfo.customArgumentMatchingFunctions)

        return Result.success(Unit)
    }

    fun getMatchingModules(hookedFunctionArguments: Array<*>): MatchingModulesIterator {
        val matchingModules: List<Priority> = argumentMatcher.matchModules(hookedFunctionArguments)
        val mergedLists = matchingModules + argumentMatchingFunctions

        return MatchingModulesIterator(hookedFunctionArguments, PriorityQueue(mergedLists))
    }

    inner class MatchingModulesIterator(val hookedFunctionArguments: Array<*>, val mergedMatchers: PriorityQueue<Priority>) : Iterator<FakerModule> {

        private val queueIterator = mergedMatchers.iterator()
        private lateinit var nextModule: FakerModule
        override fun hasNext(): Boolean {

            while (queueIterator.hasNext()) {
                val element = queueIterator.next()

                nextModule = when(element) {
                    is FakerModule.FakerArgumentCheckerFunction -> element.call(*hookedFunctionArguments).getOrNull()?.getOrNull() ?: continue
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