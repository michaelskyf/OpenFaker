package pl.michaelskyf.openfaker.ui

import android.content.SharedPreferences
import pl.michaelskyf.openfaker.ui_module_bridge.HookData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData
import pl.michaelskyf.openfaker.ui_module_bridge.MutableDataTunnel

class UIDataTunnel private constructor(
    private val sharedPreferences: SharedPreferences
): MutableDataTunnel {
    override fun set(
        className: String,
        methodName: String,
        hookData: Array<HookData>
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun edit(action: MutableDataTunnel.Editor.() -> Unit) {
        TODO("Not yet implemented")
    }

    override fun edit(): MutableDataTunnel.Editor {
        TODO("Not yet implemented")
    }

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
}