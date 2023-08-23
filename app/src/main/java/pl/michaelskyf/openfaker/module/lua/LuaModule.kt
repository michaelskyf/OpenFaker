package pl.michaelskyf.openfaker.module.lua

class LuaModule(val priority: Int): Comparable<LuaModule> {

    fun call(functionName: String, vararg functionArguments: Any?): Any? {
        TODO("Not yet implemented!")
    }

    override fun compareTo(other: LuaModule): Int {

        return priority.compareTo(other.priority)
    }
}