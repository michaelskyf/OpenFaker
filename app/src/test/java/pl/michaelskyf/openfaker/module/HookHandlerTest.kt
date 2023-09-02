package pl.michaelskyf.openfaker.module

import io.mockk.mockk
import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.TestLogger
import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel

class HookHandlerTest {

    @Test
    fun `beforeHookedMethod() should run only a matching hook marked to run before the hooked function`() {

        val dataTunnel = mockk<DataTunnel.Receiver>()
        val hookHandler = HookHandler("some.package", "some.class",
            "someMethod", TestLogger(), dataTunnel).getOrThrow()

        val thisObject = mockk<Any>()
        val method = mockk<MethodWrapper>()
        val hookParameters = HookParameters(thisObject, method, arrayOf(), null)
        hookHandler.beforeHookedMethod(hookParameters)
    }
}