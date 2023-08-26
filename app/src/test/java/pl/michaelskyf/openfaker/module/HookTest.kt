package pl.michaelskyf.openfaker.module

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.lua.LuaFakerModule
import java.lang.reflect.Method

class HookTest {

    @Test
    fun `handleLoadPackage() should hook all methods contained in methodsToBeHooked`() {
        val hookHelper = mockk<HookHelper>()
        every { hookHelper.hookMethod(any(), any()) } just runs
        val logger = mockk<Logger>(relaxed = true)
        val methodsToBeHooked = setOf<Method>(
            mockk(),
            mockk(),
            mockk()
        )
        val hook = Hook(hookHelper, methodsToBeHooked, mapOf(), logger)

        val param = mockk<LoadPackageParam>()

        hook.handleLoadPackage(param)

        verify(exactly = 3) { hookHelper.hookMethod(any(), any()) }
    }

    @Test
    fun `beforeHookedMethod() should fake the return value of the hooked function via lua script`() {
        val fakeValue = "Fake Value Set By The Script"
        val luaScript = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:require("Hello"), argument:require("World")})
            end
            
            function runModule(hookParameters)
                hookParameters:setResult("$fakeValue")
            end
        """.trimIndent()

        val hookHelper = mockk<HookHelper>()
        val logger = mockk<Logger>(relaxed = true)
        val registry = FakerModuleRegistry()
        val module = LuaFakerModule(0, luaScript).getOrThrow()
        registry.register(module).getOrThrow()
        class TestClass {
            fun testMethod(x: String, y: String): String {
                return "$x $y"
            }
        }
        val method = TestClass::class.java.methods.first()
        val hookMap = mapOf(
            Pair(
                Pair(method.declaringClass.name, method.name),
                Pair(registry, FakerModuleRegistry())
                )
        )
        val hook = Hook(hookHelper, setOf(), hookMap, logger)

        class TestParam: MethodHookParameters(method, arrayOf("Hello", "World")) {

            var realResult: Any? = null
            override var result: Any?
                get() = realResult
                set(value) { realResult = value }
        }

        val param = TestParam()
        hook.MethodHookHandler().beforeHookedMethod(param)

        assert(param.result == fakeValue)
    }

    @Test
    fun `afterHookedMethod() should fake the return value of the hooked function via lua script`() {
        val fakeValue = "Fake Value Set By The Script"
        val luaScript = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:require("Hello"), argument:require("World")})
            end
            
            function runModule(hookParameters)
                hookParameters:setResult("$fakeValue")
            end
        """.trimIndent()

        val hookHelper = mockk<HookHelper>()
        val logger = mockk<Logger>(relaxed = true)
        val registry = FakerModuleRegistry()
        val module = LuaFakerModule(0, luaScript).getOrThrow()
        registry.register(module).getOrThrow()
        class TestClass {
            fun testMethod(x: String, y: String): String {
                return "$x $y"
            }
        }
        val method = TestClass::class.java.methods.first()
        val hookMap = mapOf(
            Pair(
                Pair(method.declaringClass.name, method.name),
                Pair(FakerModuleRegistry(), registry)
            )
        )
        val hook = Hook(hookHelper, setOf(), hookMap, logger)

        class TestParam: MethodHookParameters(method, arrayOf("Hello", "World")) {

            var realResult: Any? = null
            override var result: Any?
                get() = realResult
                set(value) { realResult = value }
        }

        val param = TestParam()
        hook.MethodHookHandler().afterHookedMethod(param)

        assert(param.result == fakeValue)
    }
}