package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.xposed.ClassMethodPair
import java.lang.reflect.Method

class Hook(
    private val hookHelper: HookHelper,
    private var methodsToBeHooked: Set<Method>,
    var fakerRegistries: Map<ClassMethodPair, Pair<FakerModuleRegistry, FakerModuleRegistry>>, // TODO: Convert map to something thread-safe
    private val logger: Logger
    ) {

    fun handleLoadPackage(param: LoadPackageParam) {
        hookMethods(param)
    }

    private fun hookMethods(param: LoadPackageParam) {

        for (method in methodsToBeHooked) {
            hookHelper.hookMethod(method, MethodHookHandler())
        }
    }

    inner class MethodHookHandler {

        fun beforeHookedMethod(hookParameters: MethodHookParameters) {

            val moduleRegistry = fakerRegistries[Pair(hookParameters.method.declaringClass.name, hookParameters.method.name)]?.first
                ?: return

            runModules(hookParameters, moduleRegistry)
        }

        fun afterHookedMethod(hookParameters: MethodHookParameters) {

            val moduleRegistry = fakerRegistries[Pair(hookParameters.method.declaringClass.name, hookParameters.method.name)]?.second
                ?: return

            runModules(hookParameters, moduleRegistry)
        }

        private fun runModules(hookParameters: MethodHookParameters, moduleRegistry: FakerModuleRegistry) {
            val matchingModules = moduleRegistry.getMatchingModules(hookParameters.arguments)

            for (module in matchingModules) {
                val hookParametersCopy = hookParameters.clone() as MethodHookParameters
                val result = module.run(hookParametersCopy)
                if (result.getOrDefault(false)) {
                    hookParameters.result = hookParametersCopy.result
                    hookParameters.arguments = hookParametersCopy.arguments
                    return
                }

                result.exceptionOrNull()?.let { logger.log(it.toString()) }
            }
        }
    }
}