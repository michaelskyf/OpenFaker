package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.module.Hooker
import pl.michaelskyf.openfaker.module.LoadPackageParam

class XHook : IXposedHookLoadPackage {

    private val logger = XLogger()
    private val hookHelper = XHookHelper()
    private val moduleData = XFakerData()
    private val hooker = Hooker(hookHelper, moduleData, logger)

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) return

        logger.log("OpenFaker: New app: " + lpparam.packageName)

        reloadHooks().getOrElse { logger.log(it.toString()) }

        val param = LoadPackageParam(lpparam.packageName, lpparam.classLoader)
        hooker.hookMethods(param)

        logger.log("OpenFaker: Hooking done")
    }

    private fun reloadHooks(): Result<Unit> = runCatching {
        val newData = moduleData.all().getOrThrow()

        hooker.reloadMethodHooks(newData)
    }
}