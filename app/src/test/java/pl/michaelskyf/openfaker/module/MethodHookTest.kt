package pl.michaelskyf.openfaker.module

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pl.michaelskyf.openfaker.lua.LuaFakerModule
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder
import java.lang.reflect.Method

class MethodHookTest {
    class TestLogger: Logger() {
        override fun log(tag: String, message: String) {
            println("$tag $message")
        }

        override fun log(message: String) {
            println(message)
        }
    }

    class TestHookParameters(method: Method, private var methodResult: Any? = null, private vararg var methodArguments: Any?): MethodHookParameters(method) {
        override var arguments: Array<out Any?>
            get() = methodArguments
            set(value) { methodArguments = value }
        override var result: Any?
            get() = methodResult
            set(value) { methodResult = value }
    }

    private val logger = TestLogger()

    @Test
    fun `handleLoadPackage() should hook all distinct methods contained in methodsToBeHooked`() {
        val hookHelper = mockk<HookHelper>()
        val fakerModule = mockk<FakerModule>()
        val classLoader = this.javaClass.classLoader ?: fail("Class loader not found")
        val matchingArgumentsInfo = mockk<MatchingArgumentsInfo>()

        val methodHookHolders = setOf(
            MethodHookHolder("some.class", "someMethod", arrayOf(), fakerModule, MethodHookHolder.WhenToHook.Before),
            MethodHookHolder("some.class", "someMethod", arrayOf(), fakerModule, MethodHookHolder.WhenToHook.Before),
            MethodHookHolder("some.other.class", "someMethod", arrayOf(), fakerModule, MethodHookHolder.WhenToHook.Before),
        )

        val loadPackageParam = LoadPackageParam("some.package", classLoader)
        class TestClass { fun firstMethod() {} fun secondMethod() {} }

        every { fakerModule.getMatchingArgumentsInfo() } returns Result.success(matchingArgumentsInfo)
        every { matchingArgumentsInfo.exactMatchArguments } returns mutableListOf()
        every { matchingArgumentsInfo.customArgumentMatchingFunctions } returns mutableListOf()

        every { hookHelper.findClassesFromStrings(any(), *anyVararg()) } returns runCatching { arrayOf() }
        every { hookHelper.findMethod("some.class", any(), any(), *anyVararg()) } returns runCatching { TestClass::class.java.methods[0] }
        every { hookHelper.findMethod("some.other.class", any(), any(), *anyVararg()) } returns runCatching { TestClass::class.java.methods[1] }
        every { hookHelper.hookMethod(any(), any()) } just runs

        val methodHook = MethodHook(hookHelper, logger)
        methodHook.reloadMethodHooks(methodHookHolders)
        methodHook.handleLoadPackage(loadPackageParam)

        verify(exactly = 1) { hookHelper.hookMethod(TestClass::class.java.methods[0], any()) }
        verify(exactly = 1) { hookHelper.hookMethod(TestClass::class.java.methods[1], any()) }
    }

    @Test
    fun `beforeHookedMethod() should fake the return value of the hooked function via lua script if runModule() returns true`() {
        val luaScript = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({})
            end
            
            function runModule(hookParameters)
                hookParameters:setResult("Fake value set by the script")
                return true
            end
        """.trimIndent()
        val hookHelper = mockk<HookHelper>()
        val fakerModule = LuaFakerModule(0, luaScript).getOrThrow()
        val classLoader = this.javaClass.classLoader ?: fail("Class loader not found")
        val capturedHookHandler = slot<MethodHookHandler>()
        class TestClass { fun firstMethod() {} fun secondMethod() {} }
        val method = TestClass::class.java.methods[0]

        val methodHookHolders = setOf(
            MethodHookHolder(method.declaringClass.name, method.name, arrayOf(), fakerModule, MethodHookHolder.WhenToHook.Before),
        )

        val loadPackageParam = LoadPackageParam("some.package", classLoader)

        every { hookHelper.findClassesFromStrings(any(), *anyVararg()) } returns runCatching { arrayOf() }
        every { hookHelper.findMethod(any(), any(), any(), *anyVararg()) } returns runCatching { method }
        every { hookHelper.hookMethod(any(), callback = capture(capturedHookHandler)) } just runs

        val methodHook = MethodHook(hookHelper, logger)
        methodHook.reloadMethodHooks(methodHookHolders)
        methodHook.handleLoadPackage(loadPackageParam)

        val hookParameters = TestHookParameters(method)
        val hookHandler = capturedHookHandler.captured
        hookHandler.beforeHookedMethod(hookParameters)

        assert(hookParameters.result == "Fake value set by the script")
    }

    /*@Test
    fun `afterHookedMethod() should fake the return value of the hooked function via lua script if runModule() returns true`() {
        val fakeValue = "Fake Value Set By The Script"
        val luaScript = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:require("Hello"), argument:require("World")})
            end
            
            function runModule(hookParameters)
                hookParameters:setResult("$fakeValue")
                return true
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

        class TestParam(override var arguments: Array<out Any?>, override var result: Any? = null): MethodHookParameters(method)

        val param = TestParam(arrayOf("Hello", "World"))
        hook.MethodHookHandler().afterHookedMethod(param)

        assert(param.result == fakeValue)
    }

    @Test
    fun `beforeHookedMethod() should not fake the return value of the hooked function if the lua script does not return a boolean`() {
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

        class TestParam(override var arguments: Array<out Any?>, override var result: Any? = null): MethodHookParameters(method)

        val param = TestParam(arrayOf("Hello", "World"))
        hook.MethodHookHandler().beforeHookedMethod(param)

        assert(param.result != fakeValue)
    }

    @Test
    fun `afterHookedMethod() should not fake the return value of the hooked function if the lua script does not return a boolean`() {
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

        class TestParam(override var arguments: Array<out Any?>, override var result: Any? = null): MethodHookParameters(method)

        val param = TestParam(arrayOf("Hello", "World"))
        hook.MethodHookHandler().afterHookedMethod(param)

        assert(param.result != fakeValue)
    }

    @Test
    fun `beforeHookedMethod() should not fake the return value of the hooked function if the lua script returns false`() {
        val fakeValue = "Fake Value Set By The Script"
        val luaScript = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:require("Hello"), argument:require("World")})
            end
            
            function runModule(hookParameters)
                hookParameters:setResult("$fakeValue")
                return false
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

        class TestParam(override var arguments: Array<out Any?>, override var result: Any? = null): MethodHookParameters(method)

        val param = TestParam(arrayOf("Hello", "World"))
        hook.MethodHookHandler().beforeHookedMethod(param)

        assert(param.result != fakeValue)
    }

    @Test
    fun `afterHookedMethod() should not fake the return value of the hooked function if the lua script returns false`() {
        val fakeValue = "Fake Value Set By The Script"
        val luaScript = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:require("Hello"), argument:require("World")})
            end
            
            function runModule(hookParameters)
                hookParameters:setResult("$fakeValue")
                return false
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

        class TestParam(override var arguments: Array<out Any?>, override var result: Any? = null): MethodHookParameters(method)

        val param = TestParam(arrayOf("Hello", "World"))
        hook.MethodHookHandler().afterHookedMethod(param)

        assert(param.result != fakeValue)
    } */
}