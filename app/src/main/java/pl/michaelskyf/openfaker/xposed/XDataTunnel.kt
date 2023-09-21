package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XSharedPreferences
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel
import pl.michaelskyf.openfaker.ui_module_bridge.HookData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData

class XDataTunnel private constructor(private val sharedPreferences: XSharedPreferences): DataTunnel {

    companion object {
        operator fun invoke(): XDataTunnel {
            TODO()
        }
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