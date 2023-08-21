package pl.michaelskyf.openfaker.module

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.module.ExpectedFunctionArgument
import pl.michaelskyf.openfaker.module.Hook
import pl.michaelskyf.openfaker.module.HookHelper
import pl.michaelskyf.openfaker.module.LoadPackageParam
import pl.michaelskyf.openfaker.xposed.ClassMethodPair
import pl.michaelskyf.openfaker.xposed.MethodFakeValueArgsPair
import java.lang.reflect.Member
import java.lang.reflect.Method

class HookTest {

    class TestClass {
        fun testFunction(x: String) {

        }
    }

    private fun getMethodSafe(targetClass: Class<*>, methodName: String, vararg arguments: Class<*>): Method? {

        return try {
            targetClass.getMethod(methodName, *arguments)
        } catch (exception: SecurityException) {

            null
        } catch (exception: NoSuchMethodException) {

            null
        }
    }

    @Test
    fun `handleLoadPackage() class not found`() {

        // Prepare
        val loadPackageParameter = LoadPackageParam("com.some.package", javaClass.classLoader ?: fail("Failed to get classLoader"))

        val methodNameToArgumentsMap = mutableMapOf<ClassMethodPair, MethodFakeValueArgsPair>()
        methodNameToArgumentsMap[Pair("some.class", "someMethod")] = Pair("Fake value", arrayOf( ExpectedFunctionArgument(String(), ExpectedFunctionArgument.CompareOperation.AlwaysTrue)))

        val hookHelper = mockk<HookHelper>()
        every { hookHelper.findMethod(any(), any(), any(), *anyVararg()) } returns null

        // Run
        val hook = Hook(hookHelper, methodNameToArgumentsMap.toMutableMap(), mockk(relaxed = true))
        hook.handleLoadPackage(loadPackageParameter)

        // Check
        verify(exactly = 0) { hookHelper.hookMethod(any(), any()) }
    }

    @Test
    fun `handleLoadPackage() class found`() {

        // Prepare
        val loadPackageParameter = LoadPackageParam("com.some.package", javaClass.classLoader ?: fail("Failed to get classLoader"))

        val methodNameToArgumentsMap = mutableMapOf<ClassMethodPair, MethodFakeValueArgsPair>()
        methodNameToArgumentsMap[Pair("some.class", "someMethod")] = Pair("Fake value", arrayOf( ExpectedFunctionArgument(String(), ExpectedFunctionArgument.CompareOperation.AlwaysTrue)))

        val hookHelper = mockk<HookHelper>(relaxed = true)
        val testClass = TestClass()
        every {
            hookHelper.findMethod(any(), any(), any(), *anyVararg())
        } returns (getMethodSafe(testClass::class.java, "testFunction", String::class.java) ?: fail("Failed to get method"))

        // Run
        val hook = Hook(hookHelper, methodNameToArgumentsMap.toMutableMap(), mockk(relaxed = true))
        hook.handleLoadPackage(loadPackageParameter)

        // Check
        verify(exactly = 1) { hookHelper.hookMethod(any(), any()) }
    }

    class MethodHookHandlerTest {

        class TestHookParameters(method: Member, arguments: Array<*>) : MethodHookParameters(method, arguments) {
            override var result: Any?
                get() = null
                set(value) {}
        }
        @Test
        fun `beforeHookedMethod() should set the result when all expected arguments match function arguments`() {

            // Assemble
            val fakeValue = "Fake Value"
            val arguments = arrayOf( "Argument" )
            val map = mutableMapOf<ClassMethodPair, MethodFakeValueArgsPair>()
            val method = TestClass::class.java.methods.first()
            map[Pair(method.declaringClass.name, method.name)] = Pair(fakeValue, arrayOf(
                ExpectedFunctionArgument(arguments[0]) ))

            val hook = Hook(mockk(relaxed = true), map, mockk(relaxed = true))
            val methodHookHandler = hook.MethodHookHandler()

            mockkConstructor(TestHookParameters::class)
            val mockedParameters = TestHookParameters(method, arguments)

            // Run
            methodHookHandler.beforeHookedMethod(mockedParameters)

            // Assert
            verify(exactly = 1) { mockedParameters.result = fakeValue }
        }

        @Test
        fun `beforeHookedMethod() should not set the result when expected arguments differ from function arguments`() {

            // Assemble
            val fakeValue = "Fake Value"
            val arguments = arrayOf( "Argument" )
            val map = mutableMapOf<ClassMethodPair, MethodFakeValueArgsPair>()
            val method = TestClass::class.java.methods.first()
            map[Pair(method.declaringClass.name, method.name)] = Pair(fakeValue, arrayOf(
                ExpectedFunctionArgument("Different argument") ))

            val hook = Hook(mockk(relaxed = true), map, mockk(relaxed = true))
            val methodHookHandler = hook.MethodHookHandler()

            mockkConstructor(TestHookParameters::class)
            val mockedParameters = TestHookParameters(method, arguments)

            // Run
            methodHookHandler.beforeHookedMethod(mockedParameters)

            // Assert
            verify(exactly = 0) { mockedParameters.result = fakeValue }
        }
    }
}