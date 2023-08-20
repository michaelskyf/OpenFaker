package pl.michaelskyf.openfaker.xposed

import android.content.ContentResolver
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig

typealias ClassMethodPair = Pair<String, String>
typealias MethodFakeValueArgsPair = Pair<Any, Array<ExpectedFunctionArgument<Any>>>



class XHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private val hook = Hook(XHookHelper(), mapOf())

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedBridge.log("OpenFaker: New app: " + lpparam.packageName)

        if (lpparam.packageName == BuildConfig.APPLICATION_ID)
        {
            val method = XposedHelpers.findMethodExact("pl.michaelskyf.openfaker.xposed.PrefsListener", lpparam.classLoader, "reload")
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

        val map = JsonToMap().getMapFromJson(json)
            ?: return

        hook.methodArgs = map
    }

    inner class PreferenceCallback : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {

            XposedBridge.log("OpenFaker: Reloading preferences")

            val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, PrefsListener().prefName)

            val json = preferences.getString("xposed_method_args", null)
                ?: return

            val map = JsonToMap().getMapFromJson(json) ?: return

            hook.methodArgs = map
            param?.result = true
        }
    }
}