package pl.michaelskyf.openfaker.module

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

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

        // TODO: Rewrite
        val argument = arguments.first()
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