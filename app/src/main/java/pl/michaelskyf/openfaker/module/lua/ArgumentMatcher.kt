package pl.michaelskyf.openfaker.module.lua

import java.util.PriorityQueue

class ArgumentMatcher private constructor(
    private val match: MutableMap<Any?, ArgumentMatcher>,
    private var ignore: ArgumentMatcher?,
    private val queue: PriorityQueue<LuaModule>
){
    companion object {
        operator fun invoke(): ArgumentMatcher {
            return ArgumentMatcher(mutableMapOf(), null, PriorityQueue())
        }
    }

    fun match(arguments: Array<Any?>): PriorityQueue<LuaModule> {
        if (arguments.isEmpty()) return queue

        val resultMatch = match[arguments.first()]?.match(arguments.sliceArray(1 until arguments.size)) ?: PriorityQueue()
        val resultIgnore = ignore?.match(arguments.sliceArray(1 until arguments.size)) ?: PriorityQueue()

        return PriorityQueue(resultMatch + resultIgnore)
    }

    fun add(arguments: Array<FunctionArgument>, module: LuaModule) {
        if (arguments.isEmpty())
        {
            queue.add(module)
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