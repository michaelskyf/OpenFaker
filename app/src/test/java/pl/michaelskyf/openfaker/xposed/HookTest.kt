package pl.michaelskyf.openfaker.xposed

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test

class HookTest {

    @BeforeEach
    fun beforeEach() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
    }

    @Test
    fun `handleLoadPackage() class not found`() {

        val param = this.javaClass.classLoader?.let { LoadPackageParam("unit.test.class", it) }
            ?: fail("Failed to get the classLoader (Should never happen)")

        val classMethodPair: ClassMethodPair = Pair("unit.test.class", "test")
        val arguments: Array<TypeValuePair> = arrayOf( Pair(String::class.java, null) )
        val methodFakeValueArgsPair: MethodFakeValueArgsPair = Pair("Fake value", arguments)
        val map = mutableMapOf(Pair(classMethodPair, methodFakeValueArgsPair))
        val hookHelper = mockk<HookHelper>()
        val hook = Hook(hookHelper, map)

        hook.handleLoadPackage(param)

        verify(exactly = 1) { hookHelper.findAndHookMethod(any(), any(), any(), any(), any()) }
    }
}