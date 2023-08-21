package pl.michaelskyf.openfaker.module

import android.os.Build
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.xposed.ClassMethodPair
import pl.michaelskyf.openfaker.xposed.MethodFakeValueArgsPair
import java.lang.reflect.Method

class Hook(
    private val hookHelper: HookHelper,
    var methodArgs: Map<ClassMethodPair, MethodFakeValueArgsPair>,
    private val logger: Logger
    ) {

    fun handleLoadPackage(param: LoadPackageParam) {
        hookMethods(param)
    }

    private fun hookMethods(param: LoadPackageParam) {

        for ((key, value) in methodArgs)
        {
            val className = key.first
            val methodName = key.second

            val argumentTypes = value.second.map { it.getType().typeName }.toTypedArray()

            val method = hookHelper.findMethod(className, param.classLoader, methodName, *argumentTypes)
                ?: continue

            hookHelper.hookMethod(method, MethodHookHandler())
            logger.log("Hooked $className.$methodName()")
        }
    }

    // TODO: Check if beforeHookedMethod can be executed concurrently (Map may not be compatible)
    inner class MethodHookHandler {

        fun beforeHookedMethod(hookParameters: MethodHookParameters) {

            val expectedFunctionData = methodArgs[Pair(hookParameters.method.declaringClass.name, hookParameters.method.name)]
                ?: return

            if(shouldModifyFunctionValue(hookParameters.arguments, expectedFunctionData.second)) {

                hookParameters.result = expectedFunctionData.first
            }
        }

        private fun shouldModifyFunctionValue(realFunctionArguments: Array<*>, expectedArguments: Array<ExpectedFunctionArgument>): Boolean {

            for ((argumentIndex, expectedArgument) in expectedArguments.withIndex())
            {
                val realArgument = realFunctionArguments[argumentIndex]

                if (!expectedArgument.matches(realArgument))
                {
                    return false
                }
            }

            return true
        }
    }
}