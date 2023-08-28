package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.module.MethodHook
import pl.michaelskyf.openfaker.module.LoadPackageParam

typealias ClassMethodPair = Pair<String, String>

class XHook : IXposedHookLoadPackage {

    private val logger = XLogger()
    private val hookHelper = XHookHelper()
    private val moduleData = XFakerData()
    private val methodHook = MethodHook(hookHelper, moduleData, logger)

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != BuildConfig.APPLICATION_ID) {

            logger.log("OpenFaker: New app: " + lpparam.packageName)

            reloadHooks()

            val param = LoadPackageParam(lpparam.packageName, lpparam.classLoader)
            methodHook.hookMethods(param)
        }
    }

    private fun reloadHooks(): Result<Unit> = runCatching {
        logger.log("Reloading")
        val newMethodHooks = moduleData.methodHooks.map { it.toMethodHookHolder(logger).getOrThrow() }

        methodHook.reloadMethodHooks(newMethodHooks.toSet())
    }
}