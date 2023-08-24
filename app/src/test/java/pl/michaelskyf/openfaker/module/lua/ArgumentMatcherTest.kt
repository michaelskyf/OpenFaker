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
            FunctionArgument.ignore(),
            FunctionArgument.require("Hello"),
            FunctionArgument.require(1234),
            FunctionArgument.ignore()
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
            FunctionArgument.ignore(),
            FunctionArgument.require("Hello"),
            FunctionArgument.require(1234),
            FunctionArgument.require(null)
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

    @Test
    fun `match() should return multiple matching items`() {
        val matchingLuaModule = mockk<LuaModule>(relaxed = true)
        val notMatchingLuaModule = mockk<LuaModule>(relaxed = true)
        every { matchingLuaModule.priority } returns 0
        every { notMatchingLuaModule.priority } returns 0
        val argumentMatcher = ArgumentMatcher()

        val moduleArgumentsMatching: Array<Array<FunctionArgument>> = arrayOf(
            arrayOf(
                FunctionArgument.require("Hello"),
                FunctionArgument.require(123),
                FunctionArgument.require("Everyone!"),
                FunctionArgument.require(86.7),
                FunctionArgument.require(null)
            ),
            arrayOf(
                FunctionArgument.ignore(),
                FunctionArgument.ignore(),
                FunctionArgument.ignore(),
                FunctionArgument.ignore(),
                FunctionArgument.ignore()
            ),
            arrayOf(
                FunctionArgument.require("Hello"),
                FunctionArgument.require(123),
                FunctionArgument.ignore(),
                FunctionArgument.require(86.7),
                FunctionArgument.require(null)
            ),
        )

        val moduleArgumentsNotMatching: Array<Array<FunctionArgument>> = arrayOf(
            arrayOf(
                FunctionArgument.require(null),
                FunctionArgument.require("Hello"),
                FunctionArgument.require(1234),
                FunctionArgument.require(null)
            ),
            arrayOf(
                FunctionArgument.ignore()
            )
        )

        moduleArgumentsMatching.forEach {
            argumentMatcher.add(it, matchingLuaModule)
        }

        moduleArgumentsNotMatching.forEach {
            argumentMatcher.add(it, notMatchingLuaModule)
        }

        val queue = argumentMatcher.match(arrayOf(
            "Hello",
            123,
            "Everyone!",
            86.7,
            null
        ))

        queue.forEach {
            assert(it === matchingLuaModule)
        }
        assert(queue.size == moduleArgumentsMatching.size)
    }

    @Test
    fun `match() should match embedded FunctionArguments`() {
        val luaModule = mockk<LuaModule>()
        every { luaModule.priority } returns 0
        val argumentMatcher = ArgumentMatcher()
        val moduleArguments: Array<FunctionArgument> = arrayOf(
            FunctionArgument.ignore(),
            FunctionArgument.require("Hello"),
            FunctionArgument.require(FunctionArgument.require(1234)),
            FunctionArgument.require(FunctionArgument.require(FunctionArgument.ignore()))
        )

        argumentMatcher.add(moduleArguments, luaModule)

        val queue = argumentMatcher.match(arrayOf(
            FunctionArgument.require("Hello"),
            "Hello",
            FunctionArgument.require(1234),
            FunctionArgument.require(FunctionArgument.ignore())
        ))

        assert(!queue.isEmpty())
    }
}