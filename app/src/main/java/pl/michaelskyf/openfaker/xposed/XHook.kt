package pl.michaelskyf.openfaker.xposed

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.module.MethodHook
import pl.michaelskyf.openfaker.module.LoadPackageParam
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder
import java.lang.reflect.Type

class XHook : IXposedHookLoadPackage {

    private val logger = XLogger()
    private val hookHelper = XHookHelper()
    private val moduleData = XFakerData()
    private val methodHook = MethodHook(hookHelper, moduleData, logger)

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) return

        logger.log("OpenFaker: New app: " + lpparam.packageName)

        reloadHooks().getOrElse { logger.log(it.toString()) }

        val param = LoadPackageParam(lpparam.packageName, lpparam.classLoader)
        methodHook.hookMethods(param)
    }

    private fun reloadHooks(): Result<Unit> = runCatching {
        val newMethodHooks = mutableListOf<MethodHookHolder>()
        moduleData.all().getOrThrow().map { it.forEach { holder -> newMethodHooks.add(holder) } }

        methodHook.reloadMethodHooks(newMethodHooks.toSet())
    }
}