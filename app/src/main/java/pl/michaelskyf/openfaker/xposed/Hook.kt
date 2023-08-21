package pl.michaelskyf.openfaker.xposed

import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import pl.michaelskyf.openfaker.BuildConfig

class Hook(private val hookHelper: HookHelper, var methodArgs: Map<ClassMethodPair, MethodFakeValueArgsPair>) {

    fun handleLoadPackage(param: LoadPackageParam) {

        for ((key, value) in methodArgs)
        {
            val className = key.first
            val methodName = key.second

            val argumentTypes = value.second.map { it.getType().typeName }.toTypedArray()

            val method = try {
                hookHelper.findMethod(className, param.classLoader, methodName, *argumentTypes)
                    ?: continue
            } catch (e: NoSuchMethodException) {
                XposedBridge.log(BuildConfig.APPLICATION_ID + " $e")
                continue
            }

            hookHelper.hookMethod(method, MethodHookHandler())
        }
    }

    // TODO: Check if beforeHookedMethod is executed concurrently (MutableMap may not be compatible)
    inner class MethodHookHandler {

        fun beforeHookedMethod(hookParameters: XC_MethodHook.MethodHookParam) {

            val expectedFunctionData = methodArgs[Pair(hookParameters.method.declaringClass.name, hookParameters.method.name)]
                ?: return

            if(shouldModifyFunctionValue(hookParameters.args, expectedFunctionData.second)) {

                hookParameters.result = expectedFunctionData.first
            }
        }

        fun shouldModifyFunctionValue(realFunctionArguments: Array<Any>, expectedArguments: Array<ExpectedFunctionArgument>): Boolean {

            for ((argumentIndex, expectedArgument) in expectedArguments.withIndex())
            {
                val realArgument = realFunctionArguments[argumentIndex]

                if (!expectedArgument.matches(realArgument))
                {
                    XposedBridge.log("Dont modify")
                    return false
                }
            }

            return true
        }
    }
}