package pl.michaelskyf.openfaker.xposed

import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
import com.google.gson.Gson
import de.robv.android.xposed.XSharedPreferences
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.ui_module_bridge.FakerData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder

class XFakerData private constructor(
    private val sharedPreferences: XSharedPreferences
): FakerData() {

    companion object {
        operator fun invoke(): XFakerData {
            val sharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID, FakerData.fakerDataFileName)
            sharedPreferences.makeWorldReadable()

            return XFakerData(sharedPreferences)
        }
    }

    override var methodHooks: Array<MethodHookHolder>
        get() {
            val jsonObjects = sharedPreferences.getString(methodHooksKey, null) ?: return arrayOf()
            return Gson().fromJson(jsonObjects, Array<MethodHookHolder>::class.java) ?: return arrayOf()
        }

        set(value) {
            val result = Gson().toJson(value) ?: return
            sharedPreferences.edit(commit = true) { this.putString(methodHooksKey, result) }
        }
}