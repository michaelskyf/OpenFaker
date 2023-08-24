package pl.michaelskyf.openfaker.module.lua

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.luaj.vm2.LuaFunction

class TemporaryRegistryTest {
    @Test
    fun `exactMatchArguments() should add given arguments only to the correct list`() {
        val temporaryRegistry = TemporaryRegistry()
        val arguments = arrayOf(
            FunctionArgument.ignore(),
            FunctionArgument.require("Hello"),
            FunctionArgument.require("World"),
            FunctionArgument.require(FunctionArgument.ignore())
        )
        temporaryRegistry.exactMatchArguments(*arguments)

        assert(temporaryRegistry.exactMatchArguments.size == 1)
        assert(temporaryRegistry.exactMatchArguments.first().contentEquals(arguments))
        assert(temporaryRegistry.customArgumentMatchingFunctions.isEmpty())
    }

    /*
    How to mock LuaFunction?
    @Test
    fun `customMatchArgument() should add custom comparison function only to the correct list`() {
        val temporaryRegistry = TemporaryRegistry()
        val customFunction = mockk<LuaFunction>(relaxed = true)
        temporaryRegistry.customMatchArgument(customFunction)

        assert(temporaryRegistry.customArgumentMatchingFunctions.size == 1)
        assert(temporaryRegistry.customArgumentMatchingFunctions.first() == customFunction)
        assert(temporaryRegistry.exactMatchArguments.isEmpty())
    }*/
}