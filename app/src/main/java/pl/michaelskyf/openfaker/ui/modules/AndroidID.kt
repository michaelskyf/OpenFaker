package pl.michaelskyf.openfaker.ui.modules

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.provider.Settings
import androidx.appcompat.content.res.AppCompatResources
import pl.michaelskyf.openfaker.R
import pl.michaelskyf.openfaker.module.Priority
import pl.michaelskyf.openfaker.ui.Property
import pl.michaelskyf.openfaker.ui_module_bridge.HookData
import pl.michaelskyf.openfaker.ui_module_bridge.LuaFakerModuleFactory
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData

class AndroidID {
    companion object {
        fun getProperty(context: Context): Property {
            val icon = getIcon(context, R.drawable.ic_launcher_foreground)
            val luaSource = context.resources.openRawResource(R.raw.android_id).bufferedReader().use { it.readText() }

            val methodData = MethodData(
                "android.provider.Settings\$Secure",
                "getString",
                arrayOf(
                    HookData(
                        HookData.WhichPackages.All,
                        arrayOf("android.content.ContentResolver", "java.lang.String"),
                        LuaFakerModuleFactory(luaSource, null, 0),
                        HookData.WhenToHook.Before
                    )
                )
            )

            return Property(icon, "Android ID", { getRealValue(context) }, methodData, false)
        }

        @SuppressLint("HardwareIds")
        private fun getRealValue(context: Context): String
            = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        private fun getIcon(context: Context, id: Int): Drawable
                = AppCompatResources.getDrawable(context, id)!!
    }
}