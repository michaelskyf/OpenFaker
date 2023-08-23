package pl.michaelskyf.openfaker.module.lua

import io.mockk.every
import io.mockk.mockk

import org.junit.jupiter.api.Test

class ArgumentMatcherTest {

    @Test
    fun match() {
    }

    @Test
    fun `match() should return given item when arguments match`() {
        val luaModule = mockk<LuaModule>()
        every { luaModule.priority } returns 0
        val argumentMatcher = ArgumentMatcher()
        val moduleArguments: Array<FunctionArgument> = arrayOf(
            FunctionArgument(null,true),
            FunctionArgument("Hello"),
            FunctionArgument(1234),
            FunctionArgument(null,true)
        )

        argumentMatcher.add(moduleArguments, luaModule)

        val queue = argumentMatcher.match(arrayOf(
            "Shouldn't match this",
            "Hello",
            1234,
            "Shouldn't match this"
        ))

        assert(queue.first() === luaModule)
    }

    @Test
    fun `match() should not return given item when arguments don't match`() {
        val luaModule = mockk<LuaModule>()
        every { luaModule.priority } returns 0
        val argumentMatcher = ArgumentMatcher()
        val moduleArguments: Array<FunctionArgument> = arrayOf(
            FunctionArgument(null,true),
            FunctionArgument("Hello"),
            FunctionArgument(1234),
            FunctionArgument(null)
        )

        argumentMatcher.add(moduleArguments, luaModule)

        val queue = argumentMatcher.match(arrayOf(
            "Shouldn't match this",
            "Hello",
            1234,
            "Shouldn't match this"
        ))

        assert(queue.isEmpty())
    }
}