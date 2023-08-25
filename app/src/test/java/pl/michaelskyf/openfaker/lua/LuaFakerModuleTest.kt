package pl.michaelskyf.openfaker.lua

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class LuaFakerModuleTest {

    @Test
    fun `constructor should return failure when lua module doesn't contain registerModule()`() {
        val lua = """
            function runModule(hookParameters)
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua)

        assert(module.isFailure)
    }

    @Test
    fun `constructor should return failure when lua module doesn't contain runModule()`() {
        val lua = """
            function registerModule(moduleRegistry)
                local x = 1
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua)

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
        val module = LuaFakerModule(0, lua)

        assert(module.isSuccess)
    }

    @Test
    fun `constructor should return failure when lua module contains registerModule is not a function`() {
        val lua = """
            registerModule = 10
            
            function runModule(hookParameters)
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua)

        assert(module.isFailure)
    }

    @Test
    fun `constructor should return failure when lua module contains runModule is not a function`() {
        val lua = """
            function registerModule(moduleRegistry)
            end
            
            runModule = 10
        """.trimIndent()
        val module = LuaFakerModule(0, lua)

        assert(module.isFailure)
    }
}