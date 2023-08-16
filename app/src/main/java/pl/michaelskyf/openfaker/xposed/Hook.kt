package pl.michaelskyf.openfaker.xposed

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import pl.michaelskyf.openfaker.BuildConfig
import java.lang.Exception

class Hook(private val hookHelper: HookHelper, val functionInfoMap: MutableMap<ClassMethodPair, MethodFakeValueArgsPair>) {

    fun handleLoadPackage(param: LoadPackageParam) {

        if (param.packageName == BuildConfig.APPLICATION_ID)
        {
            return
        }

        Log.i("New app", param.packageName)

        for ((key, value) in functionInfoMap)
        {
            val className = key.first
            val methodName = key.second

            val argumentTypes = value.second.map { it.first }.toTypedArray()

            try {
                hookHelper.findAndHookMethod(className, param.classLoader, methodName, MethodHookHandler(), *argumentTypes)
            } catch (exception: Exception) {
                Log.e("Failed to hook", "$className.$methodName(): $exception")
            }

        }
    }

    // TODO: Check if beforeHookedMethod is executed concurrently (MutableMap may not be compatible)
    inner class MethodHookHandler {

        fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam) {

            // If info wasn't found, don't do anything
            val functionInfo = functionInfoMap[Pair(param.thisObject.toString(), param.method.toString())]
                ?: return

            for ((index, typeValuePair) in functionInfo.second.withIndex())
            {
                // Continue if we ignore the argument
                val arg = typeValuePair.second
                    ?: continue

                // If the argument is not equal to the passed argument, don't change the result
                if (arg != param.args[index])
                {
                    return
                }
            }

            // Should we check if result has the same type as functionInfo.first?
            param.result = functionInfo.first
        }
    }
}