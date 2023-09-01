package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel
import pl.michaelskyf.openfaker.ui_module_bridge.HookData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData

// TODO: Thread safety
class Hooker(
    private val hookHelper: HookHelper,
    private val dataTunnel: DataTunnel.Receiver,
    private val logger: Logger
    ) {

    private var methodsToBeHooked: HashSet<MethodData> = hashSetOf()

    fun reloadMethodHooks(methodDataCollection: Collection<MethodData>) {
        methodsToBeHooked = methodDataCollection.toHashSet()
    }

    fun hookMethods(param: LoadPackageParam) {

        // Classes may only be cached per-package, since specific classes may not be accessible in all packages
        val resolvedClassCache = mutableMapOf<String, Class<*>>()

        for (methodData in methodsToBeHooked) runCatching {
            val className = methodData.className
            val classLoader = param.classLoader
            val methodName = methodData.methodName

            val declaringClass = resolveDeclaringClass(methodData, classLoader, resolvedClassCache).getOrThrow()

            for (hookData in methodData.hookData) runCatching {
                val argumentTypes =
                    resolveArgumentTypes(hookData, classLoader, resolvedClassCache).getOrThrow()

                val method = hookHelper.findMethod(declaringClass, methodName, *argumentTypes).getOrThrow()

                hookHelper.hookMethod(method,
                    HookHandler(className, methodName, logger, true, dataTunnel).getOrThrow()
                )
            }
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