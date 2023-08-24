package pl.michaelskyf.openfaker.module.lua

import org.luaj.vm2.lib.jse.CoerceJavaToLua
import pl.michaelskyf.openfaker.module.FakerArgumentCheckerFunction
import pl.michaelskyf.openfaker.module.FakerModule
import java.util.PriorityQueue

class LuaModuleRegistry {

    private val argumentMatcher = ArgumentMatcher()
    private val argumentMatchingFunctions = PriorityQueue<FakerArgumentCheckerFunction>()
    fun register(module: FakerModule) {
    }

    fun forEachMatchingModule(functionArguments: Array<Any?>, block: FakerModule.() -> Unit) {
        val matchingArguments = argumentMatcher.match(functionArguments)
        val mergedPriorityQueues = matchingArguments + argumentMatchingFunctions

        for (element in mergedPriorityQueues) {
            val module = if (element is FakerArgumentCheckerFunction) {
                val result = element.call(*functionArguments)
                result.getOrNull() ?: continue
            } else {
                element as FakerModule
            }

            block(module)
        }
    }
}