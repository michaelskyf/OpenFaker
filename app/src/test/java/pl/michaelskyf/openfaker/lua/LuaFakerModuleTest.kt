package pl.michaelskyf.openfaker.lua

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.module.MethodHookParameters

class LuaFakerModuleTest {

    private fun testMemberFunction() {

    }

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

    @Test
    fun `run() should return true if the lua function returns true`() {
        val lua = """
            function registerModule(moduleRegistry)
            end
            
            function runModule(hookParameters)
                return true
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua)
        val parameters = mockk<MethodHookParameters>()

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
        val module = LuaFakerModule(0, lua)
        val parameters = mockk<MethodHookParameters>()

        assert(!module.getOrThrow().run(parameters).getOrThrow())
    }

    @Test
    fun `run() should throw an exception when the result is not Boolean`() {
        val lua = """
            function registerModule(moduleRegistry)
            end
            
            function runModule(hookParameters)
                return 10
            end
        """.trimIndent()
        val module = LuaFakerModule(0, lua)
        val parameters = mockk<MethodHookParameters>()

        assert(module.getOrThrow().run(parameters).isFailure)
    }
}