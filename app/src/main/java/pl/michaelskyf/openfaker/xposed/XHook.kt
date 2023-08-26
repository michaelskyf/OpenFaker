package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.module.MethodHook
import pl.michaelskyf.openfaker.module.LoadPackageParam
import pl.michaelskyf.openfaker.module.MethodHookHandler
import pl.michaelskyf.openfaker.module.MethodHookParameters
import pl.michaelskyf.openfaker.ui_module_bridge.FakerData

typealias ClassMethodPair = Pair<String, String>

class XHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private val hookHelper = XHookHelper()
    private val logger = XLogger()
    private val moduleData = XFakerData()
    private val methodHook = MethodHook(hookHelper, logger)

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedBridge.log("OpenFaker: New app: " + lpparam.packageName)

        when (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            true -> {
                val result = hookHelper.findMethod(FakerData::class.java.name, lpparam.classLoader, FakerData::methodHooks::get::class.java.name)
                val method = result.getOrElse {
                    logger.log(it.toString())
                    return
                }

                hookHelper.hookMethod(method, PreferenceCallback())
            }

            false -> {
                val param = LoadPackageParam(lpparam.packageName, lpparam.classLoader)
                methodHook.handleLoadPackage(param)
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {

        XposedBridge.log("OpenFaker: Initializing")

        val methodHooks = moduleData.methodHooks
        methodHook.reloadMethodHooks(methodHooks.toSet())
    }

    inner class PreferenceCallback : MethodHookHandler() {
        override fun beforeHookedMethod(param: MethodHookParameters) {

            XposedBridge.log("OpenFaker: Reloading preferences")

            val methodHooks = moduleData.methodHooks
            methodHook.reloadMethodHooks(methodHooks.toSet())
        }
    }
}