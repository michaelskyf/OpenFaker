package pl.michaelskyf.openfaker.xposed

import android.content.ContentResolver
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig

typealias ClassMethodPair = Pair<String, String>
typealias TypeValuePair = Pair<Class<*>, Any?>
typealias MethodFakeValueArgsPair = Pair<Any, Array<TypeValuePair>>



class XHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private lateinit var hook: Hook

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        val param = LoadPackageParam(lpparam.packageName, lpparam.classLoader)
        hook.handleLoadPackage(param)
    }

    // TODO: move json code to its own class
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {

        XposedBridge.log("OpenFaker: Initializing")

        val preferences = XSharedPreferences(BuildConfig.APPLICATION_ID, PrefsListener().prefName)

        val json = preferences.getString("xposed_method_args", null)
            ?: return

        val map = JsonToMap().getMapFromJson(json) ?: mapOf()

        hook = Hook(XHookHelper(), map)
    }
}