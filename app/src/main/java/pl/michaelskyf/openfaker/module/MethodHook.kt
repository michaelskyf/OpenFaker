package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.ui_module_bridge.FakerData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodHookHolder

// TODO: Thread safety
class MethodHook(
    private val hookHelper: HookHelper,
    private val fakerData: FakerData,
    private val logger: Logger
    ) {
    private data class MethodHookInfo(val className: String, val methodName: String, val argumentTypes: Array<String>)

    private var methodsToBeHooked: Set<MethodHookInfo> = setOf()

    fun reloadMethodHooks(methodHookHolders: Set<MethodHookHolder>) {
        val newMethodsToBeHooked = mutableSetOf<MethodHookInfo>()

        for (holder in methodHookHolders) {
            newMethodsToBeHooked.add(MethodHookInfo(holder.className, holder.methodName, holder.argumentTypes))
        }

        methodsToBeHooked = newMethodsToBeHooked
    }

    fun hookMethods(param: LoadPackageParam) {

        // Classes may only be cached per-package, since specific classes may not be accessible in all packages
        val resolvedClassCache = mutableMapOf<String, Class<*>>()

        for (methodInfo in methodsToBeHooked) {
            try {
                val className = methodInfo.className
                val classLoader = param.classLoader
                val methodName = methodInfo.methodName
                val argumentTypesStrings = methodInfo.argumentTypes

                val argumentTypes = argumentTypesStrings.map {
                    resolvedClassCache.getOrPut(it) { hookHelper.findClass(it, classLoader).getOrThrow() }
                }.toTypedArray()

                val classToHookMethodFrom = resolvedClassCache.getOrPut(className) { hookHelper.findClass(className, classLoader).getOrThrow() }
                val method = hookHelper.findMethod(classToHookMethodFrom, methodName, *argumentTypes).getOrThrow()

                hookHelper.hookMethod(method, MethodHookHandler(className, methodName, logger, true, fakerData).getOrThrow())
            } catch (exception: Exception) {
                logger.log(exception.toString())
            }
        }
    }
}