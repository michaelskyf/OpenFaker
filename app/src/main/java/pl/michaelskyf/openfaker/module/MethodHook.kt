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

                val argumentTypes = resolveArgumentTypes(methodInfo, classLoader, resolvedClassCache).getOrThrow()
                val declaringClass = resolveDeclaringClass(methodInfo, classLoader, resolvedClassCache).getOrThrow()

                val method = hookHelper.findMethod(declaringClass, methodName, *argumentTypes).getOrThrow()
                hookHelper.hookMethod(method, MethodHookHandler(className, methodName, logger, true, fakerData).getOrThrow())

            } catch (exception: Exception) {
                logger.log(exception.toString())
            }
        }
    }

    private fun resolveArgumentTypes(
        methodInfo: MethodHookInfo,
        classLoader: ClassLoader,
        resolvedClassCache: MutableMap<String, Class<*>>
    ) = runCatching {
        methodInfo.argumentTypes.map {
            resolvedClassCache.getOrPut(it) { hookHelper.findClass(it, classLoader).getOrThrow() }
        }.toTypedArray()
    }

    private fun resolveDeclaringClass(
        methodInfo: MethodHookInfo,
        classLoader: ClassLoader,
        resolvedClassCache: MutableMap<String, Class<*>>
    ) = runCatching {
        resolvedClassCache.getOrPut(methodInfo.className) { hookHelper.findClass(methodInfo.className, classLoader).getOrThrow() }
    }
}