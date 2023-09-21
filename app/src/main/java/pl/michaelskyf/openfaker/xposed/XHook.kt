package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.module.HookDispatcher
import pl.michaelskyf.openfaker.module.LoadPackageParam

class XHook : IXposedHookLoadPackage {

    private val logger = XLogger()
    private val hookHelper = XHookHelper()
    private val moduleData = XDataTunnel()
    private val hookDispatcher = HookDispatcher(hookHelper, moduleData, logger)

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) return

        logger.log("OpenFaker: New app: " + lpparam.packageName)

        kotlin.runCatching {
            val data = moduleData.all().getOrThrow()
            val param = LoadPackageParam(lpparam.packageName, lpparam.classLoader)
            hookDispatcher.hookMethods(data, param)
        }.onFailure { logger.log(it.toString()) }

        logger.log("OpenFaker: Hooking done")
    }
}