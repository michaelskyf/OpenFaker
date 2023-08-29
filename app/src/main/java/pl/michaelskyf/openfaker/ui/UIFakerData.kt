package pl.michaelskyf.openfaker.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import pl.michaelskyf.openfaker.ui_module_bridge.FakerData

class UIFakerData private constructor(
    private val sharedPreferences: SharedPreferences
): FakerData.Sender() {
    companion object {
        operator fun invoke(context: Context): Result<UIFakerData> {
            val preferences = try {
                context.getSharedPreferences(FakerData.fakerDataFileName, Context.MODE_WORLD_READABLE)
            } catch (exception: Exception) {
                return Result.failure(exception)
            }

            return Result.success(UIFakerData(preferences))
        }
    }

    override fun edit(action: Editor.() -> Unit) {
        val editor = UIEditor()

        action(editor)

        editor.commit()
    }

    inner class UIEditor: Editor() {
        private val editor = sharedPreferences.edit()

        override fun implPutString(key: String, value: String) {
            editor.putString(key, value)
        }

        override fun implCommit()
            = editor.commit()
    }
}