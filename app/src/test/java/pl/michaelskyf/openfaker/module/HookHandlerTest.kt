package pl.michaelskyf.openfaker.module

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.TestLogger
import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel
import pl.michaelskyf.openfaker.ui_module_bridge.FakerModuleFactory
import pl.michaelskyf.openfaker.ui_module_bridge.HookData

class HookHandlerTest {

    @Test
    fun `beforeHookedMethod() should run only a matching hook marked to run before the hooked function`() {

        val dataTunnel = mockk<DataTunnel.Receiver>()
        val fakerModuleFactory = mockk<FakerModuleFactory>()
        val fakerModule = mockk<FakerModule>()
        every { fakerModule.getMatchingArgumentsInfo() } returns Result.success(
            MatchingArgumentsInfo(
                mutableListOf(arrayOf()),
                mutableListOf()
            )
        )
        every { fakerModule.run(any()) } returns Result.success(true)
        every { fakerModuleFactory.createFakerModule(any()) } returns Result.success(fakerModule)
        val hooks = arrayOf(
            HookData(HookData.WhichPackages.All, arrayOf(), fakerModuleFactory, HookData.WhenToHook.Before),
            HookData(HookData.WhichPackages.All, arrayOf(), fakerModuleFactory, HookData.WhenToHook.After)
        )
        every { dataTunnel[any(), any()] } returns Result.success(hooks)
        every { dataTunnel.runIfChanged(any(), any(), any()) } returns Result.success(Unit)

        val hookHandler = HookHandler("some.package", "some.class",
            "someMethod", TestLogger(), dataTunnel).getOrThrow()

        val thisObject = mockk<Any>()
        val method = mockk<MethodWrapper>()
        val hookParameters = HookParameters(thisObject, method, arrayOf(), null)
        hookHandler.beforeHookedMethod(hookParameters)

        verify(exactly = 1) { fakerModule.run(any()) }
    }
}