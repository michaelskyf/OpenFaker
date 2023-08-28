package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.lua.LuaScriptHolder
import pl.michaelskyf.openfaker.module.MethodHook
import pl.michaelskyf.openfaker.module.LoadPackageParam
import pl.michaelskyf.openfaker.module.MethodHookHandler
import pl.michaelskyf.openfaker.module.MethodHookParameters
import pl.michaelskyf.openfaker.ui.UIFakerData

typealias ClassMethodPair = Pair<String, String>

class XHook : IXposedHookLoadPackage, IXposedHookZygoteInit {

    private val hookHelper = XHookHelper()
    private val logger = XLogger()
    private val moduleData = XFakerData()
    private val methodHook = MethodHook(hookHelper, logger)

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        logger.log("OpenFaker: New app: " + lpparam.packageName)

        when (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            true -> {
                val clazz = hookHelper.findClass(UIFakerData::class.java.name, lpparam.classLoader).getOrThrow()
                val ret = hookHelper.findClass(Array<LuaScriptHolder>::class.java.name, lpparam.classLoader).getOrThrow()
                val result = hookHelper.findMethod(clazz, "setMethodHooks", ret)
                val method = result.getOrElse {
                    logger.log(it.toString())
                    return
                }

                hookHelper.hookMethod(method, PreferenceCallback())
            }

            false -> {
                val param = LoadPackageParam(lpparam.packageName, lpparam.classLoader)
                methodHook.hookMethods(param)
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {

        logger.log("OpenFaker: Initializing")

        val methodHooks = moduleData.methodHooks.map { it.toMethodHookHolder().getOrThrow() }
        methodHook.reloadMethodHooks(methodHooks.toSet())
    }

    inner class PreferenceCallback : MethodHookHandler() {
        override fun beforeHookedMethod(hookParameters: MethodHookParameters) {

            logger.log("OpenFaker: Reloading preferences")

            val methodHooks = moduleData.methodHooks.map { it.toMethodHookHolder().getOrThrow() }
            methodHook.reloadMethodHooks(methodHooks.toSet())
        }
    }
}