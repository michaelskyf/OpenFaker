package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.xposed.ClassMethodPair

class Hook(
    private val hookHelper: HookHelper,
    var fakerRegistries: Map<ClassMethodPair, Pair<FakerModuleRegistry, FakerModuleRegistry>>, // TODO: Convert map to something thread-safe
    private val logger: Logger
    ) {

    fun handleLoadPackage(param: LoadPackageParam) {
        hookMethods(param)
    }

    private fun hookMethods(param: LoadPackageParam) {

    }

    inner class MethodHookHandler {

        fun beforeHookedMethod(hookParameters: MethodHookParameters) {

            val moduleRegistry = fakerRegistries[Pair(hookParameters.method.declaringClass.name, hookParameters.method.name)]?.second
                ?: return

            val matchingModules = moduleRegistry.getMatchingModules(hookParameters.arguments)

            for (module in matchingModules) {
                val result = module.run(hookParameters)
                if (result.getOrDefault(false)) break

                result.exceptionOrNull()?.let { logger.log(it.toString()) }
            }
        }

        fun afterHookedMethod(hookParameters: MethodHookParameters) {

            val moduleRegistry = fakerRegistries[Pair(hookParameters.method.declaringClass.name, hookParameters.method.name)]?.second
                ?: return

            val matchingModules = moduleRegistry.getMatchingModules(hookParameters.arguments)

            for (module in matchingModules) {
                val result = module.run(hookParameters)
                if (result.getOrDefault(false)) break

                result.exceptionOrNull()?.let { logger.log(it.toString()) }
            }
        }
    }
}