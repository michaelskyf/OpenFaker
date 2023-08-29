package pl.michaelskyf.openfaker.xposed

import com.google.gson.Gson
import de.robv.android.xposed.XSharedPreferences
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.lua.LuaFakerModuleHolder
import pl.michaelskyf.openfaker.module.Logger
import pl.michaelskyf.openfaker.ui_module_bridge.FakerData
import pl.michaelskyf.openfaker.ui_module_bridge.FakerModuleHolder
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder

class XFakerData private constructor(private val sharedPreferences: XSharedPreferences): FakerData.Receiver() {

    companion object {
        operator fun invoke(): XFakerData {
            val sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, FakerData.fakerDataFileName)
            sharedPreferences.makeWorldReadable()

            return XFakerData(sharedPreferences)
        }
    }

    override fun all(): Set<Array<MethodHookHolder>> = run {
        sharedPreferences.all.map { Gson().fromJson(it.value as String, Array<MethodHookHolder>::class.java) }.toSet()
    }

    override fun reload(): Boolean {
        if (!sharedPreferences.hasFileChanged()) return false

        sharedPreferences.reload()

        return true
    }

    override fun getString(key: String): String?
        = sharedPreferences.getString(key, null)
}