package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.module.Hook
import pl.michaelskyf.openfaker.module.LoadPackageParam
import pl.michaelskyf.openfaker.module.PrefsListener

typealias ClassMethodPair = Pair<String, String>



class XHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private val hook = Hook(XHookHelper(), setOf(), mapOf(), XLogger())

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedBridge.log("OpenFaker: New app: " + lpparam.packageName)

        if (lpparam.packageName == BuildConfig.APPLICATION_ID)
        {
            val method = XposedHelpers.findMethodExact("pl.michaelskyf.openfaker.module.PrefsListener", lpparam.classLoader, "reload")
            if (method == null)
            {
                XposedBridge.log("OpenFaker: Failed to hook PrefsListener!")
                return
            }

            XposedBridge.hookMethod(method, PreferenceCallback())
        } else {

            val param = LoadPackageParam(lpparam.packageName, lpparam.classLoader)
            hook.handleLoadPackage(param)
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {

        XposedBridge.log("OpenFaker: Initializing")

        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, PrefsListener().prefName)

        val json = preferences.getString("xposed_method_args", null)
            ?: return
    }

    inner class PreferenceCallback : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {

            XposedBridge.log("OpenFaker: Reloading preferences")

            val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, PrefsListener().prefName)

            val json = preferences.getString("xposed_method_args", null)
                ?: return
        }
    }
}