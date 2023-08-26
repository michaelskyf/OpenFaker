package pl.michaelskyf.openfaker.module

import androidx.core.content.contentValuesOf
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder
import pl.michaelskyf.openfaker.xposed.ClassMethodPair
import java.lang.reflect.Method

// TODO: Thread safety
class Hook(
    private val hookHelper: HookHelper,
    private val logger: Logger
    ) {

    private data class MethodHookInfo(val className: String, val methodName: String, val argumentTypes: Array<String>)

    private var methodsToBeHooked: Set<MethodHookInfo> = setOf()
    private var fakerRegistries: Map<ClassMethodPair, Pair<FakerModuleRegistry, FakerModuleRegistry>> = mapOf()

    fun reloadMethodHooks(methodHookHolders: Array<MethodHookHolder>) {
        val newMethodsToBeHooked = mutableSetOf<MethodHookInfo>()
        val newFakerRegistries = mutableMapOf<ClassMethodPair, Pair<FakerModuleRegistry, FakerModuleRegistry>>()

        for (holder in methodHookHolders) {
            newMethodsToBeHooked.add(MethodHookInfo(holder.className, holder.methodName, holder.argumentTypes))
            newFakerRegistries[Pair(holder.className, holder.methodName)] = Pair(FakerModuleRegistry(), FakerModuleRegistry()) // TODO: before & after
        }

        methodsToBeHooked = newMethodsToBeHooked
        fakerRegistries = newFakerRegistries
    }

    fun handleLoadPackage(param: LoadPackageParam) {
        hookMethods(param)
    }

    private fun hookMethods(param: LoadPackageParam) {

        for (methodInfo in methodsToBeHooked) {
            val mappedArgumentTypes = try {
                methodInfo.argumentTypes.map { hookHelper.findClass(it, param.classLoader).getOrThrow() }
            } catch (exception: Exception) {
                logger.log(exception.toString())
                continue
            }

            val foundMethod = hookHelper.findMethod(methodInfo.className, param.classLoader, methodInfo.methodName, mappedArgumentTypes)
            if (foundMethod.isFailure) {
                logger.log(foundMethod.exceptionOrNull().toString())
                continue
            }

            hookHelper.hookMethod(foundMethod.getOrThrow(), Handler())
        }
    }

    inner class Handler: MethodHookHandler() {

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