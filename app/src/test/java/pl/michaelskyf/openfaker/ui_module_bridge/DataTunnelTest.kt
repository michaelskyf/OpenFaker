package pl.michaelskyf.openfaker.ui_module_bridge

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import kotlin.test.assertEquals

class DataTunnelTest {
    abstract class TestReceiver : DataTunnel.Receiver() {
        public abstract override fun implReload(): Boolean
        public abstract override fun getString(key: String): String?
        public abstract override fun implAll(): Map<String, String>

    }

     abstract class TestSender: DataTunnel.Sender {
         abstract override fun edit(action: DataTunnel.Sender.Editor.() -> Unit)
         abstract override fun edit(): DataTunnel.Sender.Editor
     }

    abstract class TestEditor: DataTunnel.Sender.Editor() {
        public abstract override fun implPutString(key: String, value: String)
        public abstract override fun implCommit(): Boolean
    }

    @Test
    fun `runIfChanged() should only run given block when reload() returned true and className and methodName match`() {
        val sender = spyk<DataTunnel.Sender>()
        val receiver = spyk<TestReceiver>()
        val receiverData = hashMapOf<String, String>()
        every { receiver.implReload() } returns true
        every { receiver.implAll() } answers { receiverData }
        every { receiver.getString(any()) } answers { receiverData[firstArg()] }
        receiver.reload()

        val editor = spyk<TestEditor>()
        every { editor.implPutString(any(), any()) } answers { receiverData[firstArg()] = secondArg() }
        every { editor.implCommit() } returns true
        every { sender.edit() } returns editor

        val fakerModuleFactory = TestFakerModuleFactory()
        val hookerData1 = HookerData("some.class", "someMethod",
            arrayOf(
                HookHandlerData(HookHandlerData.WhichPackages.All(), arrayOf(), fakerModuleFactory, HookHandlerData.WhenToHook.Before)
            )
        )
        val hookerData2 = HookerData("some.other.class", "someMethod",
            arrayOf(
                HookHandlerData(HookHandlerData.WhichPackages.All(), arrayOf(), fakerModuleFactory, HookHandlerData.WhenToHook.Before)
            )
        )

        sender.edit().putMethodData(hookerData1).getOrThrow().putMethodData(hookerData2).getOrThrow().commit()
        val callback = mockk<Array<HookHandlerData>.() -> Unit>(relaxed = true)
        receiver.runIfChanged("some.class", "someMethod", callback).getOrThrow()

        verify(exactly = 1) { callback.invoke(any()) }
        verify(exactly = 1) { receiver.runIfChanged(any(), any(), any()) }
        assertEquals(receiver.all().getOrThrow().size, 2)
    }

    @Test
    fun `all() should return all stored data and if the data was not read before it should not call runIfChanged`() {
        val sender = spyk<DataTunnel.Sender>()
        val receiver = spyk<TestReceiver>()
        val receiverData = hashMapOf<String, String>()
        every { receiver.implReload() } returns false
        every { receiver.implAll() } answers { receiverData }
        every { receiver.getString(any()) } answers { receiverData[firstArg()] }
        receiver.reload()

        val editor = spyk<TestEditor>()
        every { editor.implPutString(any(), any()) } answers { receiverData[firstArg()] = secondArg() }
        every { editor.implCommit() } returns true
        every { sender.edit() } returns editor

        val fakerModuleFactory = TestFakerModuleFactory()
        val hookerData1 = HookerData("some.class", "someMethod",
            arrayOf(
                HookHandlerData(HookHandlerData.WhichPackages.All(), arrayOf(), fakerModuleFactory, HookHandlerData.WhenToHook.Before)
            )
        )
        val hookerData2 = HookerData("some.other.class", "someMethod",
            arrayOf(
                HookHandlerData(HookHandlerData.WhichPackages.All(), arrayOf(), fakerModuleFactory, HookHandlerData.WhenToHook.Before)
            )
        )

        sender.edit().putMethodData(hookerData1).getOrThrow().putMethodData(hookerData2).getOrThrow().commit()
        val receivedData = receiver.all().getOrThrow()

        receiver.runIfChanged("some.class", "someMethod") {
            fail("Shouldn't run")
        }.getOrThrow()

        assertEquals(receivedData.size, 2)
    }
}