package pl.michaelskyf.openfaker.xposed

import android.content.ContentResolver
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig

class XposedHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, XposedPrefsListener().prefName)

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            XposedBridge.log("OpenFaker: Hooking XposedPrefsListener...")
            XposedHelpers.findAndHookMethod(BuildConfig.APPLICATION_ID + ".xposed.XposedPrefsListener", lpparam.classLoader,
                "reload", ReloadSettingsHook())
            return
        }

        XposedHelpers.findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getString",
            ContentResolver::class.java, String::class.java, SettingsStringHook())
    }

    inner class SettingsStringHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) {

            val prefName = param.args[1] as? String

            preferences.getString(prefName, null)?.let {
                param.result = it
            }

        }
    }

    inner class ReloadSettingsHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {

            XposedBridge.log("OpenFaker: Reloading shared prefs")

            preferences.makeWorldReadable()

            if (!preferences.file.canRead())
            {
                XposedBridge.log("OpenFaker: Cannot read shared preferences!")
                return
            }

            preferences.reload()
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {

        XposedBridge.log("OpenFaker: Initializing")

        preferences.makeWorldReadable()

        if (!preferences.file.canRead())
        {
            XposedBridge.log("OpenFaker: Cannot read shared preferences!")
            return
        }

        preferences.reload()
    }
}