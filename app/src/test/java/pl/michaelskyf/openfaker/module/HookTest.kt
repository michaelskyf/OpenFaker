package pl.michaelskyf.openfaker.module

import android.util.Log
import io.mockk.every
import io.mockk.mockk
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
        val hook = Hook(hookHelper, methodNameToArgumentsMap.toMutableMap(), mockk<Logger>(relaxed = true))
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
        val hook = Hook(hookHelper, methodNameToArgumentsMap.toMutableMap(), mockk<Logger>(relaxed = true))
        hook.handleLoadPackage(loadPackageParameter)

        // Check
        verify(exactly = 1) { hookHelper.hookMethod(any(), any()) }
    }

    /*class MethodHookHandlerTest {
        @Test
        fun `shouldModifyFunctionValue() should return true when all expectedArguments are null`() {

            // Prepare
            val methodHookHandler = Hook(mockk<HookHelper>(relaxed = true), mapOf()).MethodHookHandler()
            val realFunctionArguments = arrayOf("Some", "Important", "Arguments")
            val expectedFunctionArguments

            // Run
            val result = methodHookHandler.shouldModifyFunctionValue()

            // Assert
        }
    }*/
}