package pl.michaelskyf.openfaker.xposed

abstract class HookHelper {
    abstract fun findAndHookMethod(className: String, classLoader: ClassLoader, methodName: String, callback: Hook.MethodHookHandler, vararg parameterTypes: Class<*>)
}