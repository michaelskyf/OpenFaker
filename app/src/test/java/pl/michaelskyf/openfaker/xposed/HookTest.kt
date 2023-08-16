package pl.michaelskyf.openfaker.xposed

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
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

    @BeforeEach
    fun beforeEach() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
    }

    @Test
    fun `handleLoadPackage() class not found`() {

        // Prepare
        val loadPackageParameter = LoadPackageParam("com.some.package", javaClass.classLoader ?: fail("Failed to get classLoader"))

        val methodNameToArgumentsMap = mutableMapOf<ClassMethodPair, MethodFakeValueArgsPair>()
        methodNameToArgumentsMap[Pair("some.class", "someMethod")] = Pair("Fake value", arrayOf( Pair( String::class.java, null ) ))

        val hookHelper = mockk<HookHelper>()
        every { hookHelper.findMethod(any(), any(), any(), *anyVararg()) } returns null

        // Run
        val hook = Hook(hookHelper, methodNameToArgumentsMap.toMutableMap())
        hook.handleLoadPackage(loadPackageParameter)

        // Check
        verify(exactly = 0) { hookHelper.hookMethod(any(), any()) }
    }

    @Test
    fun `handleLoadPackage() class found`() {

        // Prepare
        val loadPackageParameter = LoadPackageParam("com.some.package", javaClass.classLoader ?: fail("Failed to get classLoader"))

        val methodNameToArgumentsMap = mutableMapOf<ClassMethodPair, MethodFakeValueArgsPair>()
        methodNameToArgumentsMap[Pair("some.class", "someMethod")] = Pair("Fake value", arrayOf( Pair( String::class.java, null ) ))

        val hookHelper = mockk<HookHelper>(relaxed = true)
        val testClass = TestClass()
        every {
            hookHelper.findMethod(any(), any(), any(), *anyVararg())
        } returns (getMethodSafe(testClass::class.java, "testFunction", String::class.java) ?: fail("Failed to get method"))

        // Run
        val hook = Hook(hookHelper, methodNameToArgumentsMap.toMutableMap())
        hook.handleLoadPackage(loadPackageParameter)

        // Check
        verify(exactly = 1) { hookHelper.hookMethod(any(), any()) }
    }
}