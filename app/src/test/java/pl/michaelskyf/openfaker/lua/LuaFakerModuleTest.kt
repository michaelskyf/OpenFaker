package pl.michaelskyf.openfaker.lua

import io.mockk.mockk

import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.module.FunctionArgument
import pl.michaelskyf.openfaker.module.Logger
import pl.michaelskyf.openfaker.module.HookParameters

class LuaFakerModuleTest {

    private fun testMemberFunction() {

    }

    private val logger = mockk<Logger>()

    @Test
    fun `constructor should return failure when lua module doesn't contain registerModule()`() {
        val lua = """
            function runModule(hookParameters)
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        assert(module.isFailure)
    }

    @Test
    fun `constructor should return failure when lua module doesn't contain runModule()`() {
        val lua = """
            function registerModule(moduleRegistry)
                local x = 1
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        assert(module.isFailure)
    }

    @Test
    fun `constructor should return success when lua module contains registerModule() and runModule()`() {
        val lua = """
            function registerModule(moduleRegistry)
            end
            
            function runModule(hookParameters)
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        assert(module.isSuccess)
    }

    @Test
    fun `constructor should return failure when lua module contains registerModule is not a function`() {
        val lua = """
            registerModule = 10
            
            function runModule(hookParameters)
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        assert(module.isFailure)
    }

    @Test
    fun `constructor should return failure when lua module contains runModule is not a function`() {
        val lua = """
            function registerModule(moduleRegistry)
            end
            
            runModule = 10
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        assert(module.isFailure)
    }

    @Test
    fun `run() should return true if the lua function returns true`() {
        val lua = """
            function registerModule(moduleRegistry)
            end
            
            function runModule(hookParameters)
                return true
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)
        val parameters = mockk<HookParameters>()

        assert(module.getOrThrow().run(parameters).getOrThrow())
    }

    @Test
    fun `run() should return false if the lua function returns false`() {
        val lua = """
            function registerModule(moduleRegistry)
            end
            
            function runModule(hookParameters)
                return false
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)
        val parameters = mockk<HookParameters>()

        assert(!module.getOrThrow().run(parameters).getOrThrow())
    }

    @Test
    fun `run() should return failure when the result is not Boolean`() {
        val lua = """
            function registerModule(moduleRegistry)
            end
            
            function runModule(hookParameters)
                return 10
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)
        val parameters = mockk<HookParameters>()

        assert(module.getOrThrow().run(parameters).isFailure)
    }

    @Test
    fun `getMatchingArgumentsInfo() should return failure when registerModule call throws an exception`() {
        val lua = """
            function registerModule(moduleRegistry)
                moduleRegistry:thisMethodDoesNotExist()
            end
            
            function runModule(hookParameters)
                return true
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        val result = module.getOrThrow().getMatchingArgumentsInfo()

        assert(result.isFailure)
    }

    @Test
    fun `getMatchingArgumentsInfo() should return failure when arguments given to exactMatchArguments() are not FunctionArgument(s)`() {
        val lua = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments("Not", "Function", "Arguments")
            end
            
            function runModule(hookParameters)
                return true
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        val result = module.getOrThrow().getMatchingArgumentsInfo()

        assert(result.isFailure)
    }

    @Test
    fun `getMatchingArgumentsInfo() should return a single correct exact match`() {
        val lua = """
            function registerModule(moduleRegistry)
                local first = argument:ignore()
                local second = argument:require("Function")
                local third = argument:require("Arguments")
                moduleRegistry:exactMatchArguments({first, second, third})
            end
            
            function runModule(hookParameters)
                return true
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        val result = module.getOrThrow().getMatchingArgumentsInfo().getOrThrow()

        assert(result.exactMatchArguments.size == 1)
        assert(result.customArgumentMatchingFunctions.isEmpty())
        val returnedExact = result.exactMatchArguments.first()
        assert(returnedExact.contentEquals(arrayOf(
           FunctionArgument.ignore(),
            FunctionArgument.require("Function"),
            FunctionArgument.require("Arguments")
        )))
    }

    @Test
    fun `getMatchingArgumentsInfo() should return all matchers set by the registerModule()`() {
        val lua = """
            function customMatcher()
            end
            
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({})
                moduleRegistry:exactMatchArguments({argument:require("A"), argument:require("B")})
                
                moduleRegistry:customMatchArgument(customMatcher)
            end
            
            function runModule(hookParameters)
                return true
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        val result = module.getOrThrow().getMatchingArgumentsInfo().getOrThrow()
        val exact = result.exactMatchArguments
        val custom = result.customArgumentMatchingFunctions

        assert(exact.size == 2)
        assert(custom.size == 1)
    }

    @Test
    fun `custom matcher call which takes no arguments should fail if it does not return bool`() {
        val lua = """
            function customMatcher()
            end
            
            function registerModule(moduleRegistry)
                moduleRegistry:customMatchArgument(customMatcher)
            end
            
            function runModule(hookParameters)
                return true
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        val arguments = module.getOrThrow().getMatchingArgumentsInfo().getOrThrow()
        val custom = arguments.customArgumentMatchingFunctions.first()

        val result = custom.call()
        assert(result.isFailure)
    }

    @Test
    fun `custom matcher should return a FakerModule if it returns true`() {
        val lua = """
            function customMatcher()
                return true
            end
            
            function registerModule(moduleRegistry)
                moduleRegistry:customMatchArgument(customMatcher)
            end
            
            function runModule(hookParameters)
                return true
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        val arguments = module.getOrThrow().getMatchingArgumentsInfo().getOrThrow()
        val custom = arguments.customArgumentMatchingFunctions.first()

        val result = custom.call().getOrThrow()
        assert(result.isPresent)
    }

    @Test
    fun `custom matcher should return an empty option if it returns false`() {
        val lua = """
            function customMatcher()
                return false
            end
            
            function registerModule(moduleRegistry)
                moduleRegistry:customMatchArgument(customMatcher)
            end
            
            function runModule(hookParameters)
                return true
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua, logger)

        val arguments = module.getOrThrow().getMatchingArgumentsInfo().getOrThrow()
        val custom = arguments.customArgumentMatchingFunctions.first()

        val result = custom.call().getOrThrow()
        assert(!result.isPresent)
    }
}