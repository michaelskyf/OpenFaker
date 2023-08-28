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

    private val logger = TestLogger()

    class TestMethodWrapper(method: Method): MethodWrapper(method) {
        override fun invoke(thisObject: Any?, vararg arguments: Any?) {
            method.invoke(thisObject, arguments)
        }

    }

    class TestHookParameters(method: Method, private var methodArguments: Array<Any?> = arrayOf()): MethodHookParameters(null, TestMethodWrapper(method)) {

        private var methodResult: Any? = null
        override var arguments: Array<Any?>
            get() = methodArguments
            set(value) { methodArguments = value }
        override var result: Any?
            get() = methodResult
            set(value) { methodResult = value }
    }



    @Test
    fun `hookMethods() should hook all distinct methods contained in methodsToBeHooked`() {
        val hookHelper = mockk<HookHelper>()
        val fakerModule = mockk<FakerModule>()
        val classLoader = this.javaClass.classLoader ?: fail("Class loader not found")
        val matchingArgumentsInfo = mockk<MatchingArgumentsInfo>()

        class TestClass { fun firstMethod() {} }
        class TestClassOther { fun firstMethod() {} }
        val firstMethod = TestClass::class.java.methods.first()
        val secondMethod = TestClass::class.java.methods[1]

        val methodHookHolders = setOf(
            MethodHookHolder("some.class", "someMethod", arrayOf(), fakerModule, MethodHookHolder.WhenToHook.Before),
            MethodHookHolder("some.class", "someMethod", arrayOf(), fakerModule, MethodHookHolder.WhenToHook.Before),
            MethodHookHolder("some.other.class", "someMethod", arrayOf(), fakerModule, MethodHookHolder.WhenToHook.Before),
        )

        val loadPackageParam = LoadPackageParam("some.package", classLoader)


        every { fakerModule.getMatchingArgumentsInfo() } returns Result.success(matchingArgumentsInfo)
        every { matchingArgumentsInfo.exactMatchArguments } returns mutableListOf()
        every { matchingArgumentsInfo.customArgumentMatchingFunctions } returns mutableListOf()

        every { hookHelper.findClass("some.class", any()) } returns runCatching { TestClass::class.java }
        every { hookHelper.findClass("some.other.class", any()) } returns runCatching { TestClassOther::class.java }
        every { hookHelper.findMethod(TestClass::class.java, any(), *arrayOf()) } returns runCatching { firstMethod }
        every { hookHelper.findMethod(TestClassOther::class.java, any(), *arrayOf()) } returns runCatching { secondMethod }
        every { hookHelper.hookMethod(any(), any()) } just runs

        val methodHook = MethodHook(hookHelper, logger)
        methodHook.reloadMethodHooks(methodHookHolders)
        methodHook.hookMethods(loadPackageParam)

        verify(exactly = 1) { hookHelper.hookMethod(firstMethod, any()) }
        verify(exactly = 1) { hookHelper.hookMethod(secondMethod, any()) }
    }

    @Test
    fun `beforeHookedMethod() should fake the return value of the hooked function via lua script if runModule() returns true`() {
        val luaScript = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:require("Argument")})
            end
            
            function runModule(hookParameters)
                local arguments = hookParameters:getArguments()
                arguments[1] = "Fake Argument"
                
                hookParameters:setResult("Fake value set by the script")
                return true
            end
        """.trimIndent()
        val hookHelper = mockk<HookHelper>()
        val fakerModule = LuaFakerModule(0, luaScript, logger).getOrThrow()
        val classLoader = this.javaClass.classLoader ?: fail("Class loader not found")
        val capturedHookHandler = slot<MethodHookHandler>()
        class TestClass { fun firstMethod() {} fun secondMethod() {} }
        val method = TestClass::class.java.methods[0]
        val className = method.declaringClass.name

        val methodHookHolders = setOf(
            MethodHookHolder(className, method.name, arrayOf(), fakerModule, MethodHookHolder.WhenToHook.Before),
        )

        val loadPackageParam = LoadPackageParam("some.package", classLoader)

        every { hookHelper.findClass(className, any()) } returns runCatching { TestClass::class.java }
        every { hookHelper.findMethod(any(), any()) } returns runCatching { method }
        every { hookHelper.hookMethod(any(), callback = capture(capturedHookHandler)) } just runs

        val methodHook = MethodHook(hookHelper, logger)
        methodHook.reloadMethodHooks(methodHookHolders)
        methodHook.hookMethods(loadPackageParam)

        val hookParameters = TestHookParameters(method, arrayOf("Argument"))
        val hookHandler = capturedHookHandler.captured
        hookHandler.beforeHookedMethod(hookParameters)

        assert(hookParameters.arguments.first() == "Fake Argument")
        assert(hookParameters.result == "Fake value set by the script")
    }

    @Test
    fun `afterHookedMethod() should fake the return value and the given argument of the hooked function via lua script if runModule() returns true`() {
        val luaScript = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:require("Argument")})
            end
            
            function runModule(hookParameters)
                local arguments = hookParameters:getArguments()
                arguments[1] = "Fake Argument"
                
                hookParameters:setResult("Fake value set by the script")
                return true
            end
        """.trimIndent()
        val hookHelper = mockk<HookHelper>()
        val fakerModule = LuaFakerModule(0, luaScript, logger).getOrThrow()
        val classLoader = this.javaClass.classLoader ?: fail("Class loader not found")
        val capturedHookHandler = slot<MethodHookHandler>()
        class TestClass { fun firstMethod() {} fun secondMethod() {} }
        val method = TestClass::class.java.methods[0]
        val className = method.declaringClass.name

        val methodHookHolders = setOf(
            MethodHookHolder(method.declaringClass.name, method.name, arrayOf(), fakerModule, MethodHookHolder.WhenToHook.After),
        )

        val loadPackageParam = LoadPackageParam("some.package", classLoader)

        every { hookHelper.findClass(className, any()) } returns runCatching { TestClass::class.java }
        every { hookHelper.findMethod(any(), any()) } returns runCatching { method }
        every { hookHelper.hookMethod(any(), callback = capture(capturedHookHandler)) } just runs

        val methodHook = MethodHook(hookHelper, logger)
        methodHook.reloadMethodHooks(methodHookHolders)
        methodHook.hookMethods(loadPackageParam)

        val hookParameters = TestHookParameters(method, arrayOf("Argument"))
        val hookHandler = capturedHookHandler.captured
        hookHandler.afterHookedMethod(hookParameters)

        assert(hookParameters.arguments.first() == "Fake Argument")
        assert(hookParameters.result == "Fake value set by the script")
    }

    @Test
    fun `afterHookedMethod() should not fake the return value nor the given argument of the hooked function via lua script if runModule() returns false`() {
        val luaScript = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:require("Argument")})
            end
            
            function runModule(hookParameters)
                local arguments = hookParameters:getArguments()
                arguments[1] = "Fake Argument"
                
                hookParameters:setResult("Fake value set by the script")
                return false
            end
        """.trimIndent()
        val hookHelper = mockk<HookHelper>()
        val fakerModule = LuaFakerModule(0, luaScript, logger).getOrThrow()
        val classLoader = this.javaClass.classLoader ?: fail("Class loader not found")
        val capturedHookHandler = slot<MethodHookHandler>()
        class TestClass { fun firstMethod() {} fun secondMethod() {} }
        val method = TestClass::class.java.methods[0]
        val className = method.declaringClass.name

        val methodHookHolders = setOf(
            MethodHookHolder(className, method.name, arrayOf(), fakerModule, MethodHookHolder.WhenToHook.After),
        )

        val loadPackageParam = LoadPackageParam("some.package", classLoader)

        every { hookHelper.findClass(className, any()) } returns runCatching { TestClass::class.java }
        every { hookHelper.findMethod(any(), any(), *arrayOf()) } returns runCatching { method }
        every { hookHelper.hookMethod(any(), callback = capture(capturedHookHandler)) } just runs

        val methodHook = MethodHook(hookHelper, logger)
        methodHook.reloadMethodHooks(methodHookHolders)
        methodHook.hookMethods(loadPackageParam)

        val hookParameters = TestHookParameters(method, arrayOf("Argument"))
        val hookHandler = capturedHookHandler.captured
        hookHandler.afterHookedMethod(hookParameters)

        assert(hookParameters.arguments.first() == "Argument")
        assert(hookParameters.result == null)
    }

    @Test
    fun `afterHookedMethod() should not fake the return value nor the given argument of the hooked function via lua script if runModule() does not return any value`() {
        val luaScript = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:require("Argument")})
            end
            
            function runModule(hookParameters)
                local arguments = hookParameters:getArguments()
                arguments[1] = "Fake Argument"
                
                hookParameters:setResult("Fake value set by the script")
            end
        """.trimIndent()
        val hookHelper = mockk<HookHelper>()
        val fakerModule = LuaFakerModule(0, luaScript, logger).getOrThrow()
        val classLoader = this.javaClass.classLoader ?: fail("Class loader not found")
        val capturedHookHandler = slot<MethodHookHandler>()
        class TestClass { fun firstMethod() {} fun secondMethod() {} }
        val method = TestClass::class.java.methods[0]
        val className = method.declaringClass.name

        val methodHookHolders = setOf(
            MethodHookHolder(className, method.name, arrayOf(), fakerModule, MethodHookHolder.WhenToHook.After),
        )

        val loadPackageParam = LoadPackageParam("some.package", classLoader)

        every { hookHelper.findClass(className, any()) } returns runCatching { TestClass::class.java }
        every { hookHelper.findMethod(any(), any()) } returns runCatching { method }
        every { hookHelper.hookMethod(any(), callback = capture(capturedHookHandler)) } just runs

        val methodHook = MethodHook(hookHelper, logger)
        methodHook.reloadMethodHooks(methodHookHolders)
        methodHook.hookMethods(loadPackageParam)

        val hookParameters = TestHookParameters(method, arrayOf("Argument"))
        val hookHandler = capturedHookHandler.captured
        hookHandler.afterHookedMethod(hookParameters)

        assert(hookParameters.arguments.first() == "Argument")
        assert(hookParameters.result == null)
    }

    @Test
    fun `hookMethods() should not throw any exception when hooking functions`() {
        val hookHelper = mockk<HookHelper>()
        val methodHook = MethodHook(hookHelper, logger)

        class TestClass { fun validMethod() {} }
        val method = TestClass::class.java.methods.first()

        every { hookHelper.findClass("valid.class", any()) } returns runCatching { TestClass::class.java }
        every { hookHelper.findClass("InvalidType", any()) } throws
                Exception("Class not found")
        every { hookHelper.findClass(String::class.java.name, any()) } returns
                runCatching { String::class.java }
        every { hookHelper.findMethod(any(), "invalidMethod") } throws
                Exception("Method not found")
        every { hookHelper.findMethod(any(), "validMethod", *arrayOf(String::class.java)) } returns
                runCatching { method }
        every { hookHelper.hookMethod(any(), any()) } just runs

        val fakerModule = mockk<FakerModule>()
        every { fakerModule.getMatchingArgumentsInfo() } returns runCatching { mockk(relaxed = true) }

        val methodHookHolders = setOf(
            MethodHookHolder("valid.class", "validMethod", arrayOf(String::class.java.name),
                fakerModule, MethodHookHolder.WhenToHook.Before),
            MethodHookHolder("invalid.class", "validMethod", arrayOf(String::class.java.name),
                fakerModule, MethodHookHolder.WhenToHook.Before),
            MethodHookHolder("valid.class", "invalidMethod", arrayOf(String::class.java.name),
                fakerModule, MethodHookHolder.WhenToHook.Before),
            MethodHookHolder("valid.class", "validMethod", arrayOf("invalidType"),
                fakerModule, MethodHookHolder.WhenToHook.Before)
        )
        methodHook.reloadMethodHooks(methodHookHolders)

        val param = mockk<LoadPackageParam>()
        every { param.classLoader } returns this.javaClass.classLoader!!

        methodHook.hookMethods(param)

        verify(exactly = 1) { hookHelper.hookMethod(any(), any()) }
    }
}