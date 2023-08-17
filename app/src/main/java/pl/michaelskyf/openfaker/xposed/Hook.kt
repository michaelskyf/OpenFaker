package pl.michaelskyf.openfaker.xposed

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import pl.michaelskyf.openfaker.BuildConfig

class Hook(private val hookHelper: HookHelper, var methodArgs: Map<ClassMethodPair, MethodFakeValueArgsPair>) {

    fun handleLoadPackage(param: LoadPackageParam) {

        XposedBridge.log(BuildConfig.APPLICATION_ID + " Hello, entries: " + methodArgs.size)
        for ((key, value) in methodArgs)
        {
            val className = key.first
            val methodName = key.second

            val argumentTypes = value.second.map { it.first }.toTypedArray()

            XposedBridge.log(BuildConfig.APPLICATION_ID + " Hooking '$className.$methodName()'")
            val method = try {
                hookHelper.findMethod(className, param.classLoader, methodName, *argumentTypes)
                    ?: continue
            } catch (e: NoSuchMethodException) {
                XposedBridge.log(BuildConfig.APPLICATION_ID + " $e")
                continue
            }

            XposedBridge.log(BuildConfig.APPLICATION_ID + " Hooked successfully")
            hookHelper.hookMethod(method, MethodHookHandler())
        }
    }

    // TODO: Check if beforeHookedMethod is executed concurrently (MutableMap may not be compatible)
    inner class MethodHookHandler {

        fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam) {

            XposedBridge.log(BuildConfig.APPLICATION_ID + " " + param.method.declaringClass.name + param.method.name)
            // If info wasn't found, don't do anything
            val functionArgs = methodArgs[Pair(param.method.declaringClass.name, param.method.name)]
                ?: return

            for ((index, typeValuePair) in functionArgs.second.withIndex())
            {
                // Continue if we ignore the argument
                val arg = typeValuePair.second
                    ?: continue

                // If the argument is not equal to the passed argument, don't change the result
                if (arg != param.args[index])
                {
                    XposedBridge.log(BuildConfig.APPLICATION_ID + " Invalid argument")
                    return
                }
            }

            XposedBridge.log(BuildConfig.APPLICATION_ID + " Fake value: " + functionArgs.first)
            // Should we check if result has the same type as functionInfo.first?
            param.result = functionArgs.first
        }
    }
}