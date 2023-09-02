package pl.michaelskyf.openfaker.ui_module_bridge

import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class DataTunnelReceiverTest {
    abstract class TestReceiver : DataTunnel.Receiver() {
        public abstract override fun implReload(): Boolean
        public abstract override fun getString(key: String): String?
        public abstract override fun getAll(): Map<String, String>

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
        every { receiver.getAll() } answers { receiverData }
        every { receiver.getString(any()) } answers { receiverData[firstArg()] }
        receiver.reload()

        val editor = spyk<TestEditor>()
        every { editor.implPutString(any(), any()) } answers { receiverData[firstArg()] = secondArg() }
        every { editor.implCommit() } returns true
        every { sender.edit() } returns editor

        val fakerModuleFactory = mockk<FakerModuleFactory>()
        val methodData1 = MethodData("some.class", "someMethod",
            arrayOf(
                HookData(HookData.WhichPackages.All, arrayOf(), fakerModuleFactory, HookData.WhenToHook.Before)
            )
        )
        val methodData2 = MethodData("some.other.class", "someMethod",
            arrayOf(
                HookData(HookData.WhichPackages.All, arrayOf(), fakerModuleFactory, HookData.WhenToHook.Before)
            )
        )

        sender.edit().putMethodData(methodData1).getOrThrow().putMethodData(methodData2).getOrThrow().commit()
        val callback = mockk<Array<HookData>.() -> Unit>()
        receiver.runIfChanged("some.class", "someMethod", callback)

        verify(exactly = 1) { callback.invoke(any()) }
        verify(exactly = 1) { receiver.runIfChanged(any(), any(), any()) }
    }
}