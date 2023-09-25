package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.module.HookDispatcher
import pl.michaelskyf.openfaker.module.LoadPackageParam

class XHook : IXposedHookLoadPackage {

    private val logger = XLogger()
    private val hookHelper = XHookHelper()
    private val dataTunnel = XSharedPreferencesDataTunnel(XSharedPreferences(BuildConfig.APPLICATION_ID, "open_faker_module_method_hooks"))

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) return

        logger.log("New app: " + lpparam.packageName)

        runCatching {
            val data = dataTunnel.getAllHooks().getOrThrow()
            val param = LoadPackageParam(lpparam.packageName, lpparam.classLoader)
            val hookDispatcher = HookDispatcher(hookHelper, dataTunnel, logger)
            hookDispatcher.hookMethods(data, param)
        }.onFailure { logger.log(it.toString()) }

        logger.log("Hooking done")
    }
}