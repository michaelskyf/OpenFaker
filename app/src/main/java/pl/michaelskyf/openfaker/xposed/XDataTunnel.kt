package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XSharedPreferences
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel

class XDataTunnel private constructor(private val sharedPreferences: XSharedPreferences): DataTunnel.Receiver() {
    private val fakerDataFileName = "open_faker_module_method_hooks"

    companion object {
        private const val fakerDataFileName = "open_faker_module_method_hooks"
        operator fun invoke(): XDataTunnel {
            val sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, fakerDataFileName)
            sharedPreferences.makeWorldReadable()

            return XDataTunnel(sharedPreferences)
        }
    }

    override fun implReload(): Boolean {
        if (!sharedPreferences.hasFileChanged()) return false

        sharedPreferences.reload()

        return true
    }

    override fun getString(key: String): String?
        = sharedPreferences.getString(key, null)

    override fun implAll(): Map<String, String>
        = sharedPreferences.all as Map<String, String>
}