package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.ui_module_bridge.FakerData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder
import java.util.Optional

// TODO: Thread safety
class MethodHookHandler private constructor(
    private val className: String,
    private val methodName: String,
    private val logger: Logger,
    private val isDynamic: Boolean,
    private val fakerData: FakerData,
    private var fakerRegistries: Pair<FakerModuleRegistry, FakerModuleRegistry>
    ) {

    companion object {
        operator fun invoke(className: String, methodName: String, logger: Logger, isDynamic: Boolean, fakerData: FakerData): Result<MethodHookHandler> = runCatching {
            val registries = loadRegistries(className, methodName, fakerData, logger).getOrThrow()

            MethodHookHandler(className, methodName, logger, isDynamic, fakerData, registries)
        }

        private fun loadRegistries(className: String, methodName: String, fakerData: FakerData, logger: Logger)
            : Result<Pair<FakerModuleRegistry, FakerModuleRegistry>> = runCatching {
            val methodHookHolders = fakerData[className, methodName].getOrThrow()
            val beforeRegistry = FakerModuleRegistry()
            val afterRegistry = FakerModuleRegistry()

            for (holder in methodHookHolders) {
                when (holder.whenToHook) {
                    MethodHookHolder.WhenToHook.Before -> beforeRegistry.register(holder.fakerModule).getOrElse { logger.log(it.toString()) }
                    MethodHookHolder.WhenToHook.After -> afterRegistry.register(holder.fakerModule).getOrElse { logger.log(it.toString()) }
                }
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
        if (fakerData.reload()) return@runCatching

        fakerRegistries = loadRegistries(className, methodName, fakerData, logger).getOrThrow()
    }

    private fun runModules(hookParameters: MethodHookParameters, moduleRegistry: FakerModuleRegistry) {
        val matchingModules = moduleRegistry.getMatchingModules(hookParameters.arguments)

        for (module in matchingModules) {
            val hookParametersCopy = TemporaryMethodHookParameters(hookParameters)
            val result = module.run(hookParametersCopy)
            if (result.getOrDefault(false)) {
                hookParameters.result = hookParametersCopy.result
                hookParameters.arguments = hookParametersCopy.arguments
                return
            }

            result.exceptionOrNull()?.let { logger.log(it.toString()) }
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