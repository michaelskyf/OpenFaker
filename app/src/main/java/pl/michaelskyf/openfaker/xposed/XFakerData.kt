package pl.michaelskyf.openfaker.xposed

import com.google.gson.Gson
import de.robv.android.xposed.XSharedPreferences
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.lua.LuaScriptHolder
import pl.michaelskyf.openfaker.module.Logger
import pl.michaelskyf.openfaker.ui_module_bridge.FakerData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder

class XFakerData private constructor(
    private val logger: Logger,
    private val sharedPreferences: XSharedPreferences
): FakerData() {

    companion object {
        operator fun invoke(logger: Logger): XFakerData {
            val sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, FakerData.fakerDataFileName)
            sharedPreferences.makeWorldReadable()

            return XFakerData(logger, sharedPreferences)
        }
    }

    override fun get(className: String, methodName: String): Result<Array<MethodHookHolder>> = runCatching {
        val json = sharedPreferences.getString("$className.$methodName", null)
            ?: throw Exception("Failed to get json from $className.$methodName")

        val hookHolders = Gson().fromJson(json, Array<LuaScriptHolder>::class.java)

        hookHolders.map { it.toMethodHookHolder(logger).getOrThrow() }.toTypedArray()
    }

    override fun set(className: String, methodName: String, json: String) {
        TODO("Not yet implemented")
    }

    override fun all(): Set<Array<LuaScriptHolder>> = run {
        sharedPreferences.all.map { Gson().fromJson(it.value as String, Array<LuaScriptHolder>::class.java) }.toSet()
    }

    override fun reload(): Boolean {
        if (!sharedPreferences.hasFileChanged()) return false

        sharedPreferences.reload()

        return true
    }
}