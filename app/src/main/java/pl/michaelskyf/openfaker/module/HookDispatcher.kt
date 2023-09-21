package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel
import pl.michaelskyf.openfaker.ui_module_bridge.HookData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData

// TODO: Thread safety
class HookDispatcher(
    private val hookHelper: HookHelper,
    private val dataTunnel: DataTunnel.Receiver,
    private val logger: Logger
    ) {
    fun hookMethods(methodsToHook: List<MethodData>, param: LoadPackageParam) {

        // Classes may only be cached per-package, since specific classes may not be accessible in all packages
        val resolvedClassCache = mutableMapOf<String, Class<*>>()

        for (methodData in methodsToHook) runCatching {
            val className = methodData.className
            val classLoader = param.classLoader
            val methodName = methodData.methodName
            val packageName = param.packageName
            val hooks = methodData.hookData.filter { it.whichPackages.isMatching(packageName) }.toTypedArray()
            if (hooks.isEmpty()) return@runCatching

            val hookHandler = HookHandler(packageName, className, methodName, logger, dataTunnel, hooks).getOrThrow()
            val declaringClass = resolveDeclaringClass(methodData, classLoader, resolvedClassCache).getOrThrow()

            for (hookData in hooks) runCatching {

                val argumentTypes = resolveArgumentTypes(hookData, classLoader, resolvedClassCache).getOrThrow()

                val method = hookHelper.findMethod(declaringClass, methodName, *argumentTypes).getOrThrow()

                hookHelper.hookMethod(method, hookHandler)
            }.onFailure { logger.log(it.toString()) }
        }.onFailure { logger.log(it.toString()) }
    }

    private fun resolveArgumentTypes(
        methodInfo: HookData,
        classLoader: ClassLoader,
        resolvedClassCache: MutableMap<String, Class<*>>
    ) = runCatching {
        methodInfo.argumentTypes.map {
            resolvedClassCache.getOrPut(it) { hookHelper.findClass(it, classLoader).getOrThrow() }
        }.toTypedArray()
    }

    private fun resolveDeclaringClass(
        methodInfo: MethodData,
        classLoader: ClassLoader,
        resolvedClassCache: MutableMap<String, Class<*>>
    ) = runCatching {
        resolvedClassCache.getOrPut(methodInfo.className) { hookHelper.findClass(methodInfo.className, classLoader).getOrThrow() }
    }
}