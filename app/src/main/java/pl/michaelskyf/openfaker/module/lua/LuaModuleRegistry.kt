package pl.michaelskyf.openfaker.module.lua

import org.luaj.vm2.lib.jse.CoerceJavaToLua
import java.util.PriorityQueue

class LuaModuleRegistry {

    private val argumentMatcher = ArgumentMatcher()
    private val argumentMatchingFunctions = PriorityQueue<ArgumentMatchingFunction>()
    fun register(module: LuaModule) {
    }

    fun forEachMatchingModule(functionArguments: Array<Any?>, block: LuaModule.() -> Unit) {
        val matchingArguments = argumentMatcher.match(functionArguments)
        val mergedPriorityQueues = matchingArguments + argumentMatchingFunctions

        for (element in mergedPriorityQueues) {
            val module = if (element is ArgumentMatchingFunction) {
                val isMatching = element.luaFunction.invoke(CoerceJavaToLua.coerce(functionArguments)).optboolean(0, false)
                when (isMatching) {
                    true -> element.module
                    false -> continue
                }
            } else {
                element as LuaModule
            }

            block(module)
        }
    }
}