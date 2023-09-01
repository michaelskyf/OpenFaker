package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XSharedPreferences
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel

class XFakerData private constructor(private val sharedPreferences: XSharedPreferences): DataTunnel.Receiver() {

    companion object {
        operator fun invoke(): XFakerData {
            val sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, DataTunnel.fakerDataFileName)
            sharedPreferences.makeWorldReadable()

            return XFakerData(sharedPreferences)
        }
    }

    override fun implReload(): Boolean {
        if (!sharedPreferences.hasFileChanged()) return false

        sharedPreferences.reload()

        return true
    }

    override fun getString(key: String): String?
        = sharedPreferences.getString(key, null)

    override fun getAll(): Map<String, String>
        = sharedPreferences.all as Map<String, String>
}