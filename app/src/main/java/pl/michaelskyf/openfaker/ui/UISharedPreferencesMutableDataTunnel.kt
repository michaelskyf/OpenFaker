package pl.michaelskyf.openfaker.ui

import android.content.SharedPreferences
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.protobuf.ProtoBuf
import pl.michaelskyf.openfaker.ui_module_bridge.HookData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData
import pl.michaelskyf.openfaker.ui_module_bridge.MutableDataTunnel

@OptIn(ExperimentalSerializationApi::class)
class UISharedPreferencesMutableDataTunnel(private val prefs: SharedPreferences): MutableDataTunnel {
    override fun set(
        className: String,
        methodName: String,
        hookData: Array<HookData>
    ): Result<Unit> = runCatching {
        edit().putMethodData(MethodData(className, methodName, hookData)).getOrThrow().commit()
    }

    override fun edit(action: MutableDataTunnel.Editor.() -> Unit) {
        action(Editor(prefs.edit()))
    }

    override fun edit(): MutableDataTunnel.Editor
        = Editor(prefs.edit())

    override fun get(className: String, methodName: String): Result<MethodData> = runCatching {
        val key = "$className.$methodName"
        val encodedObject = prefs.getString(key, null)
            ?: throw Exception("Failed to get json from $key")

        ProtoBuf.decodeFromHexString(encodedObject)
    }

    override fun hasHookChanged(className: String, methodName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAllHooks(): Result<List<MethodData>> {
        TODO("Not yet implemented")
    }

    class Editor(private val editor: SharedPreferences.Editor): MutableDataTunnel.Editor {
        private val modifiedKeys = mutableSetOf<String>()
        override fun putMethodData(methodData: MethodData): Result<MutableDataTunnel.Editor> = runCatching {
            val key = "${methodData.className}.${methodData.methodName}"
            val encodedObject = ProtoBuf.encodeToHexString(methodData)

            editor.putString(key, encodedObject)
            modifiedKeys.add(key)

            this
        }

        override fun commit(): Boolean {
            val json = ProtoBuf.encodeToHexString(modifiedKeys.toTypedArray())
            editor.putString("modifiedKeys", json)

            return editor.commit()
        }
    }
}