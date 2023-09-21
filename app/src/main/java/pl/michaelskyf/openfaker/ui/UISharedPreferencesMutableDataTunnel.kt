package pl.michaelskyf.openfaker.ui

import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.michaelskyf.openfaker.ui_module_bridge.HookData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData
import pl.michaelskyf.openfaker.ui_module_bridge.MutableDataTunnel

class UISharedPreferencesMutableDataTunnel(private val prefs: SharedPreferences): MutableDataTunnel {
    override fun set(
        className: String,
        methodName: String,
        hookData: Array<HookData>
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun edit(action: MutableDataTunnel.Editor.() -> Unit) {
        action(Editor(prefs.edit()))
    }

    override fun edit(): MutableDataTunnel.Editor
        = Editor(prefs.edit())

    override fun get(className: String, methodName: String): Result<Array<HookData>> {
        TODO("Not yet implemented")
    }

    override fun hasHookChanged(className: String, methodName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAllHooks(): Result<List<MethodData>> {
        TODO("Not yet implemented")
    }

    override fun reload(): Boolean {
        TODO("Not yet implemented")
    }

    class Editor(private val editor: SharedPreferences.Editor): MutableDataTunnel.Editor {
        private val modifiedKeys = mutableSetOf<String>()
        override fun putMethodData(methodData: MethodData): Result<MutableDataTunnel.Editor> = runCatching {
            val key = "${methodData.className}.${methodData.methodName}"
            val json = Json.encodeToString(methodData)

            editor.putString(key, json)
            modifiedKeys.add(key)

            this
        }

        override fun commit(): Boolean {
            val json = Json.encodeToString(modifiedKeys.toTypedArray())
            editor.putString("modifiedKeys", json)

            return editor.commit()
        }
    }
}