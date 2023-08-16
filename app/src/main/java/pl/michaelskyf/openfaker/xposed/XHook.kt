package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.Exception
import java.util.logging.Logger

typealias ClassMethodPair = Pair<String, String>
typealias TypeValuePair = Pair<Class<*>, *>
typealias MethodFakeValueArgsPair = Pair<Any, Array<TypeValuePair>>

class XHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private val hook = Hook(XHookHelper(), mutableMapOf())

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        val param = LoadPackageParam(lpparam.packageName, lpparam.classLoader)
        hook.handleLoadPackage(param)
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {

        XposedBridge.log("OpenFaker: Initializing")

        val classMethodPair = Pair("com.android.providers.settings.SettingsProvider", "getString")
        val arguments: Array<TypeValuePair> = arrayOf( Pair(String::class.java, "android_id") )
        val fakeValueArgsPair: MethodFakeValueArgsPair = Pair("Fake Value", arguments)
        hook.functionInfoMap[classMethodPair] = fakeValueArgsPair
    }
}