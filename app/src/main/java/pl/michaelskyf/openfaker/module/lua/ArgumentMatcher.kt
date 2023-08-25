package pl.michaelskyf.openfaker.module.lua

import kotlinx.coroutines.flow.merge
import pl.michaelskyf.openfaker.module.FakerModule
import java.util.PriorityQueue

class ArgumentMatcher private constructor(
    private val match: MutableMap<Any?, ArgumentMatcher>,
    private var ignore: ArgumentMatcher?,
    private val list: MutableList<FakerModule>
){
    companion object {
        operator fun invoke(): ArgumentMatcher {
            return ArgumentMatcher(mutableMapOf(), null, mutableListOf())
        }
    }

    fun match(arguments: Array<*>): List<FakerModule> {
        if (arguments.isEmpty()) return list

        val resultMatch = match[arguments.first()]?.match(arguments.sliceArray(1 until arguments.size)) ?: listOf()
        val resultIgnore = ignore?.match(arguments.sliceArray(1 until arguments.size)) ?: listOf()

        return resultMatch + resultIgnore
    }

    fun add(arguments: Array<out FunctionArgument>, module: FakerModule) {
        if (arguments.isEmpty())
        {
            list.add(module)
            return
        }

        val argument = arguments.first()
        // TODO: Rework
        when (argument.shouldIgnore) {
            true -> if (ignore != null ) {
                ignore!!.add(arguments.sliceArray(1 until arguments.size), module)
            } else {
                ignore = ArgumentMatcher()
                ignore!!.add(arguments.sliceArray(1 until arguments.size), module)
            }
            false -> if(match[argument.value] != null) {
                match[argument.value]!!.add(arguments.sliceArray(1 until arguments.size), module)
            } else {
                val matcher = ArgumentMatcher()
                matcher.add(arguments.sliceArray(1 until arguments.size), module)
                match[argument.value] = matcher
            }
        }
    }
}