package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.ui_module_bridge.FakerData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder

// TODO: Thread safety
class MethodHookHandler private constructor(
    private val className: String,
    private val methodName: String,
    private val logger: Logger,
    private val isDynamic: Boolean,
    private val fakerData: FakerData.Receiver,
    private var fakerRegistries: Pair<FakerModuleRegistry, FakerModuleRegistry>
    ) {

    companion object {
        operator fun invoke(className: String, methodName: String, logger: Logger, isDynamic: Boolean, fakerData: FakerData.Receiver): Result<MethodHookHandler> = runCatching {
            val methodHookHolders = fakerData[className, methodName].getOrThrow()
            val registries = loadRegistries(methodHookHolders, logger).getOrThrow()

            MethodHookHandler(className, methodName, logger, isDynamic, fakerData, registries)
        }

        private fun loadRegistries(methodHookHolders: Array<MethodHookHolder>, logger: Logger)
            : Result<Pair<FakerModuleRegistry, FakerModuleRegistry>> = runCatching {
            val beforeRegistry = FakerModuleRegistry()
            val afterRegistry = FakerModuleRegistry()

            for (holder in methodHookHolders) {
                val registry = when (holder.whenToHook) {
                    MethodHookHolder.WhenToHook.Before -> beforeRegistry
                    MethodHookHolder.WhenToHook.After -> afterRegistry
                }

                registry.register(holder.fakerModuleFactory.createFakerModule(logger).getOrThrow()).getOrElse { logger.log(it.toString()) }
            }

            Pair(beforeRegistry, afterRegistry)
        }
    }

    fun beforeHookedMethod(hookParameters: MethodHookParameters) {

        if (isDynamic) reloadRegistries().getOrElse { logger.log(it.toString()) }

        val moduleRegistry = fakerRegistries.first

        runModules(hookParameters, moduleRegistry)
    }

    fun afterHookedMethod(hookParameters: MethodHookParameters) {

        if (isDynamic) reloadRegistries().getOrElse { logger.log(it.toString()) }

        val moduleRegistry = fakerRegistries.second

        runModules(hookParameters, moduleRegistry)
    }

    private fun reloadRegistries(): Result<Unit> = runCatching {
        fakerData.runIfChanged(className, methodName) {
            fakerRegistries = loadRegistries(this, logger).getOrThrow()
        }
    }

    private fun runModules(hookParameters: MethodHookParameters, moduleRegistry: FakerModuleRegistry) {
        val matchingModules = moduleRegistry.getMatchingModules(hookParameters.arguments)

        for (module in matchingModules) {
            val hookParametersCopy = TemporaryMethodHookParameters(hookParameters)
            val result = module.run(hookParametersCopy)
            if (result.getOrDefault(false)) {
                hookParameters.result = hookParametersCopy.result
                hookParameters.arguments = hookParametersCopy.arguments
            } else {
                result.exceptionOrNull()?.let { logger.log(it.toString()) }
            }
        }
    }

    private class TemporaryMethodHookParameters private constructor(
        override val thisObject: Any?,
        override val method: MethodWrapper,
        override var arguments: Array<Any?>,
        override var result: Any?
    ): MethodHookParameters(thisObject, method) {

        companion object {
            operator fun invoke(methodHookParameters: MethodHookParameters): TemporaryMethodHookParameters
                    = TemporaryMethodHookParameters(methodHookParameters.thisObject, methodHookParameters.method,
                methodHookParameters.arguments.clone(), methodHookParameters.result)
        }
    }
}