package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder
import pl.michaelskyf.openfaker.xposed.ClassMethodPair
import pl.michaelskyf.openfaker.xposed.XMethodHookParameters
import java.lang.reflect.Method

// TODO: Thread safety
class MethodHook(
    private val hookHelper: HookHelper,
    private val logger: Logger
    ) {
    private data class MethodHookInfo(val className: String, val methodName: String, val argumentTypes: Array<String>)

    private var methodsToBeHooked: Set<MethodHookInfo> = setOf()
    private var fakerRegistries: Map<ClassMethodPair, Pair<FakerModuleRegistry, FakerModuleRegistry>> = mapOf()

    fun reloadMethodHooks(methodHookHolders: Set<MethodHookHolder>) {
        val newMethodsToBeHooked = mutableSetOf<MethodHookInfo>()
        val newFakerRegistries = mutableMapOf<ClassMethodPair, Pair<FakerModuleRegistry, FakerModuleRegistry>>()

        for (holder in methodHookHolders) {
            newMethodsToBeHooked.add(MethodHookInfo(holder.className, holder.methodName, holder.argumentTypes))

            val (registryBefore, registryAfter) = newFakerRegistries.getOrPut(Pair(holder.className, holder.methodName)) {
                Pair( FakerModuleRegistry(), FakerModuleRegistry() )
            }

            when (holder.whenToHook) {
                MethodHookHolder.WhenToHook.Before -> registryBefore.register(holder.fakerModule)
                MethodHookHolder.WhenToHook.After -> registryAfter.register(holder.fakerModule)
            }
        }

        methodsToBeHooked = newMethodsToBeHooked
        fakerRegistries = newFakerRegistries
    }

    fun hookMethods(param: LoadPackageParam) {

        for (methodInfo in methodsToBeHooked) {
            try {
                val argumentTypes = hookHelper.findClassesFromStrings(param.classLoader, *methodInfo.argumentTypes).getOrThrow()
                val method = hookHelper.findMethod(methodInfo.className, param.classLoader, methodInfo.methodName, *argumentTypes).getOrThrow()
                hookHelper.hookMethod(method, Handler())
            } catch (exception: Exception) {
                logger.log(exception.toString())
            }
        }
    }

    private inner class Handler: MethodHookHandler() {

        override fun beforeHookedMethod(hookParameters: MethodHookParameters) {

            val moduleRegistry = fakerRegistries[Pair(hookParameters.method.declaringClass.name, hookParameters.method.name)]?.first
                ?: return

            runModules(hookParameters, moduleRegistry)
        }

        override fun afterHookedMethod(hookParameters: MethodHookParameters) {

            val moduleRegistry = fakerRegistries[Pair(hookParameters.method.declaringClass.name, hookParameters.method.name)]?.second
                ?: return

            runModules(hookParameters, moduleRegistry)
        }

        private fun runModules(hookParameters: MethodHookParameters, moduleRegistry: FakerModuleRegistry) {
            val matchingModules = moduleRegistry.getMatchingModules(hookParameters.arguments)

            for (module in matchingModules) {
                val hookParametersCopy = TemporaryMethodHookParameters(hookParameters.method, hookParameters.arguments.clone(), hookParameters.result)
                val result = module.run(hookParametersCopy)
                if (result.getOrDefault(false)) {
                    hookParameters.result = hookParametersCopy.result
                    hookParameters.arguments = hookParametersCopy.arguments
                    return
                }

                result.exceptionOrNull()?.let { logger.log(it.toString()) }
            }
        }

        private inner class TemporaryMethodHookParameters(
            override val method: Method,
            override var arguments: Array<Any?>,
            override var result: Any?
        ): MethodHookParameters(method)
    }
}