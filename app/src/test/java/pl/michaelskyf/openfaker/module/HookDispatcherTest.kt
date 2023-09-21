package pl.michaelskyf.openfaker.module

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.TestLogger
import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel
import pl.michaelskyf.openfaker.ui_module_bridge.FakerModuleFactory
import pl.michaelskyf.openfaker.ui_module_bridge.HookData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData

class HookDispatcherTest {

    @Test
    fun `hookMethods() should hook 2 methods with the same name of the same class to the same HookHandler`() {
        val hookHelper = mockk<HookHelper>()
        val dataTunnel = mockk<DataTunnel.Receiver>()
        val hookDispatcher = HookDispatcher(hookHelper, dataTunnel, TestLogger())

        class TestClass {
            fun method() {}
            fun method(x: String) {}
        }
        val method1 = TestClass::class.java.methods[0]
        val method2 = TestClass::class.java.methods[1]

        every { hookHelper.findClass(TestClass::class.java.name, any()) } returns Result.success(TestClass::class.java)
        every { hookHelper.findClass(String::class.java.name, any()) } returns Result.success(String::class.java)
        every { hookHelper.findMethod(TestClass::class.java, "method") } returns Result.success(method1)
        every { hookHelper.findMethod(TestClass::class.java, "method", String::class.java) } returns Result.success(method2)
        every { hookHelper.hookMethod(any(), any()) } just runs

        val fakerModule = mockk<FakerModule>()
        every { fakerModule.getMatchingArgumentsInfo() } returns runCatching { MatchingArgumentsInfo(
            mutableListOf(), mutableListOf()) }
        val fakerModuleFactory = mockk<FakerModuleFactory>()
        every { fakerModuleFactory.createFakerModule(any()) } returns runCatching { fakerModule }
        val hooks = listOf(
            MethodData(TestClass::class.java.name, "method",
                arrayOf(
                    HookData(HookData.WhichPackages.All(), arrayOf(), fakerModuleFactory, HookData.WhenToHook.Before),
                    HookData(HookData.WhichPackages.All(), arrayOf(String::class.java.name), fakerModuleFactory, HookData.WhenToHook.After)
                )
            )
        )

        val loadPackageParam = LoadPackageParam("some.package", this.javaClass.classLoader!!)
        hookDispatcher.hookMethods(hooks, loadPackageParam)

        val handler1 = slot<HookHandler>()
        val handler2 = slot<HookHandler>()
        verify(exactly = 1) { hookHelper.hookMethod(method1, capture(handler1)) }
        verify(exactly = 1) { hookHelper.hookMethod(method2, capture(handler2)) }

        assert(handler1.captured === handler2.captured)
    }

    @Test
    fun `hookMethods() should hook 2 methods of different classes to different HookHandlers`() {
        val hookHelper = mockk<HookHelper>()
        val dataTunnel = mockk<DataTunnel.Receiver>()
        val hookDispatcher = HookDispatcher(hookHelper, dataTunnel, TestLogger())

        class TestClass {
            fun method() {}
        }

        class TestClass2 {
            fun method() {}
        }
        val method1 = TestClass::class.java.methods[0]
        val method2 = TestClass2::class.java.methods[1]

        every { hookHelper.findClass(TestClass::class.java.name, any()) } returns Result.success(TestClass::class.java)
        every { hookHelper.findClass(TestClass2::class.java.name, any()) } returns Result.success(TestClass2::class.java)
        every { hookHelper.findMethod(TestClass::class.java, "method") } returns Result.success(method1)
        every { hookHelper.findMethod(TestClass2::class.java, "method") } returns Result.success(method2)
        every { hookHelper.hookMethod(any(), any()) } just runs

        val fakerModule = mockk<FakerModule>()
        every { fakerModule.getMatchingArgumentsInfo() } returns runCatching { MatchingArgumentsInfo(
            mutableListOf(), mutableListOf()) }
        val fakerModuleFactory = mockk<FakerModuleFactory>()
        every { fakerModuleFactory.createFakerModule(any()) } returns runCatching { fakerModule }
        val hooks = listOf(
            MethodData(TestClass::class.java.name, "method",
                arrayOf(HookData(HookData.WhichPackages.All(), arrayOf(), fakerModuleFactory, HookData.WhenToHook.Before))
            ),
            MethodData(TestClass2::class.java.name, "method",
                arrayOf(HookData(HookData.WhichPackages.All(), arrayOf(), fakerModuleFactory, HookData.WhenToHook.Before))
            )
        )

        val loadPackageParam = LoadPackageParam("some.package", this.javaClass.classLoader!!)
        hookDispatcher.hookMethods(hooks, loadPackageParam)

        val handler1 = slot<HookHandler>()
        val handler2 = slot<HookHandler>()
        verify(exactly = 1) { hookHelper.hookMethod(method1, capture(handler1)) }
        verify(exactly = 1) { hookHelper.hookMethod(method2, capture(handler2)) }

        assert(handler1.captured !== handler2.captured)
    }
}