package pl.michaelskyf.openfaker.ui

import android.content.Context
import android.content.SharedPreferences
import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel

class UIDataTunnel private constructor(
    private val sharedPreferences: SharedPreferences
): DataTunnel.Sender {
    companion object {
        private const val fakerDataFileName = "open_faker_module_method_hooks"
        operator fun invoke(context: Context): Result<UIDataTunnel> = runCatching {
            UIDataTunnel(context.getSharedPreferences(fakerDataFileName, Context.MODE_WORLD_READABLE))
        }
    }

    override fun edit(action: DataTunnel.Sender.Editor.() -> Unit) {
        val editor = UIEditor()

        action(editor)

        editor.commit()
    }

    override fun edit(): DataTunnel.Sender.Editor
        = UIEditor()

    inner class UIEditor: DataTunnel.Sender.Editor() {
        private val editor = sharedPreferences.edit()

        override fun implPutString(key: String, value: String) {
            editor.putString(key, value)
        }

        override fun implCommit()
            = editor.commit()
    }
}