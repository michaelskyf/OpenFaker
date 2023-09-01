package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel
import pl.michaelskyf.openfaker.ui_module_bridge.HookData

// TODO: Thread safety
class HookHandler private constructor(
    private val packageName: String,
    private val className: String,
    private val methodName: String,
    private val logger: Logger,
    private val dataTunnel: DataTunnel.Receiver,
    private var fakerRegistries: Pair<FakerModuleRegistry, FakerModuleRegistry>
    ) {

    companion object {
        operator fun invoke(
            packageName: String,
            className: String,
            methodName: String,
            logger: Logger,
            dataTunnel: DataTunnel.Receiver
        ): Result<HookHandler> = runCatching {
            val methodHookHolders = dataTunnel[className, methodName].getOrThrow()
            val registries = loadRegistries(packageName, methodHookHolders, logger).getOrThrow()

            HookHandler(packageName, className, methodName, logger, dataTunnel, registries)
        }

        private fun loadRegistries(packageName: String, hookData: Array<HookData>, logger: Logger)
            : Result<Pair<FakerModuleRegistry, FakerModuleRegistry>> = runCatching {
            val beforeRegistry = FakerModuleRegistry()
            val afterRegistry = FakerModuleRegistry()

            val hooks = hookData.filter { it.whichPackages.isMatching(packageName) }
            for (holder in hooks) {
                val registry = when (holder.whenToHook) {
                    HookData.WhenToHook.Before -> beforeRegistry
                    HookData.WhenToHook.After -> afterRegistry
                }

                registry.register(holder.fakerModuleFactory.createFakerModule(logger).getOrThrow()).getOrElse { logger.log(it.toString()) }
            }

            Pair(beforeRegistry, afterRegistry)
        }
    }

    fun beforeHookedMethod(hookParameters: MethodHookParameters) {

        updateRegistries()

        val moduleRegistry = fakerRegistries.first

        runModules(hookParameters, moduleRegistry)
    }

    fun afterHookedMethod(hookParameters: MethodHookParameters) {

        updateRegistries()

        val moduleRegistry = fakerRegistries.second

        runModules(hookParameters, moduleRegistry)
    }

    private fun updateRegistries() {
        dataTunnel.runIfChanged(className, methodName) {
            fakerRegistries = loadRegistries(packageName, this, logger).getOrThrow()
        }.onFailure { logger.log(it.toString()) }
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