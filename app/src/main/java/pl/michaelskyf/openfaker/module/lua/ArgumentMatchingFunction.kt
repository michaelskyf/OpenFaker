package pl.michaelskyf.openfaker.module.lua

import org.luaj.vm2.LuaFunction
import pl.michaelskyf.openfaker.module.lua.LuaModule

data class ArgumentMatchingFunction(val luaFunction: LuaFunction, val module: LuaModule)
