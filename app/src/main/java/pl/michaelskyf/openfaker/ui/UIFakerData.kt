package pl.michaelskyf.openfaker.ui

import android.content.Context
import android.content.SharedPreferences
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

    override var methodHooks: Array<MethodHookHolder>
        get() = TODO("Not yet implemented")
        set(value) {}
}