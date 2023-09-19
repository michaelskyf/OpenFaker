package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel
import pl.michaelskyf.openfaker.ui_module_bridge.HookerData

// TODO: Thread safety
class Hooker(
    private val hookHelper: HookHelper,
    private val dataTunnel: DataTunnel.Receiver,
    private val logger: Logger
    ) {

    fun hookMethods(hooks: , param: LoadPackageParam) {

        // Classes may only be cached per-package, since specific classes may not be accessible in all packages
        val resolvedClassCache = mutableMapOf<String, Class<*>>()

        for (methodData in methodsToBeHooked) runCatching {
            val className = methodData.className
            val classLoader = param.classLoader
            val methodName = methodData.methodName
            val packageName = param.packageName
            val argumentTypes = methodData.argumentTypes

            val hookHandler = HookHandler(packageName, className, methodName, logger, dataTunnel).getOrThrow()
            val declaringClass = resolveDeclaringClass(methodData, classLoader, resolvedClassCache).getOrThrow()

            val resolvedArgumentTypes = resolveArgumentTypes(argumentTypes, classLoader, resolvedClassCache).getOrThrow()

            val method = hookHelper.findMethod(declaringClass, methodName, *resolvedArgumentTypes).getOrThrow()

            hookHelper.hookMethod(method, hookHandler)
        }.onFailure { logger.log(it.toString()) }
    }

    private fun resolveArgumentTypes(
        argumentTypes: Array<String>,
        classLoader: ClassLoader,
        resolvedClassCache: MutableMap<String, Class<*>>
    ) = runCatching {
        argumentTypes.map {
            resolvedClassCache.getOrPut(it) { hookHelper.findClass(it, classLoader).getOrThrow() }
        }.toTypedArray()
    }

    private fun resolveDeclaringClass(
        methodInfo: HookerData,
        classLoader: ClassLoader,
        resolvedClassCache: MutableMap<String, Class<*>>
    ) = runCatching {
        resolvedClassCache.getOrPut(methodInfo.className) { hookHelper.findClass(methodInfo.className, classLoader).getOrThrow() }
    }
}