package pl.michaelskyf.openfaker.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import pl.michaelskyf.openfaker.lua.LuaScriptHolder
import pl.michaelskyf.openfaker.ui_module_bridge.FakerData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder

class UIFakerData private constructor(
    private val sharedPreferences: SharedPreferences
): FakerData() {
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

    override fun get(className: String, methodName: String): Result<Array<MethodHookHolder>> = runCatching {
        TODO()
    }

    override fun set(className: String, methodName: String, json: String) {
        sharedPreferences.edit(commit = true) { this.putString("$className.$methodName", json) }
    }

    override fun all(): Set<Array<LuaScriptHolder>> {
        TODO("Not yet implemented")
    }

    override fun reload(): Boolean
        = false // Should it always return false?
}