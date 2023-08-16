package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook
import pl.michaelskyf.openfaker.BuildConfig

class Hook(private val hookHelper: HookHelper, val functionInfoMap: MutableMap<ClassMethodPair, MethodFakeValueArgsPair>) {

    fun handleLoadPackage(param: LoadPackageParam) {

        if (param.packageName == BuildConfig.APPLICATION_ID)
        {
            return
        }

        for ((key, value) in functionInfoMap)
        {
            val className = key.first
            val methodName = key.second

            val argumentTypes = value.second.map { it.first }.toTypedArray()

            val method = hookHelper.findMethod(className, param.classLoader, methodName, *argumentTypes)
                ?: continue

            hookHelper.hookMethod(method, MethodHookHandler())
        }
    }

    // TODO: Check if beforeHookedMethod is executed concurrently (MutableMap may not be compatible)
    inner class MethodHookHandler {

        fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam) {

            // If info wasn't found, don't do anything
            val functionInfo = functionInfoMap[Pair(param.method.declaringClass.name, param.method.name)]
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