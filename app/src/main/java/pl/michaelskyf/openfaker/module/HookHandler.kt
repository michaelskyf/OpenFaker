package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel
import pl.michaelskyf.openfaker.ui_module_bridge.HookData

// TODO: Thread safety
class HookHandler private constructor(
    private val packageName: String,
    private val className: String,
    private val methodName: String,
    private val logger: Logger,
    private val dataTunnel: DataTunnel,
    private var fakerRegistries: Pair<FakerModuleRegistry, FakerModuleRegistry>
    ) {

    companion object {
        operator fun invoke(
            packageName: String,
            className: String,
            methodName: String,
            logger: Logger,
            dataTunnel: DataTunnel,
            hooks: Array<HookData>
        ): Result<HookHandler> = runCatching {
            val registries = Pair(FakerModuleRegistry(), FakerModuleRegistry())

            for (holder in hooks) {
                val registry = when (holder.whenToHook) {
                    HookData.WhenToHook.Before -> registries.first
                    HookData.WhenToHook.After -> registries.second
                }

                registry.register(holder.fakerModuleFactory.createFakerModule(logger).getOrThrow()).getOrElse { logger.log(it.toString()) }
            }

            HookHandler(packageName, className, methodName, logger, dataTunnel, registries)
        }
    }

    fun beforeHookedMethod(hookParameters: HookParameters) {

        updateRegistries()

        val moduleRegistry = fakerRegistries.first

        runModules(hookParameters, moduleRegistry)
    }

    fun afterHookedMethod(hookParameters: HookParameters) {

        updateRegistries()

        val moduleRegistry = fakerRegistries.second

        runModules(hookParameters, moduleRegistry)
    }

    private fun updateRegistries() {
        if (dataTunnel.hasHookChanged(className, methodName)) runCatching {
            val registries = Pair(FakerModuleRegistry(), FakerModuleRegistry())

            val hooks = dataTunnel[className, methodName].getOrThrow().filter { it.whichPackages.isMatching(packageName) }
            for (holder in hooks) {
                val registry = when (holder.whenToHook) {
                    HookData.WhenToHook.Before -> registries.first
                    HookData.WhenToHook.After -> registries.second
                }

                registry.register(holder.fakerModuleFactory.createFakerModule(logger).getOrThrow()).getOrElse { logger.log(it.toString()) }
            }

            fakerRegistries = registries
        }.onFailure { logger.log(it.toString()) }
    }

    private fun runModules(hookParameters: HookParameters, moduleRegistry: FakerModuleRegistry) {
        val matchingModules = moduleRegistry.getMatchingModules(hookParameters.arguments)

        for (module in matchingModules) {
            val hookParametersClone = hookParameters.clone() as HookParameters
            val result = module.run(hookParametersClone)
            if (result.getOrDefault(false)) {
                hookParameters.result = hookParametersClone.result
                hookParameters.arguments = hookParametersClone.arguments
                return
            } else {
                result.exceptionOrNull()?.let { logger.log(it.toString()) }
            }
        }
    }
}