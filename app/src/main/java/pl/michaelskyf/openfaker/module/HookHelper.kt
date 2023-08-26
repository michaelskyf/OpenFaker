package pl.michaelskyf.openfaker.module

import java.lang.reflect.Field
import java.lang.reflect.Method

abstract class HookHelper {
    abstract fun findMethod(className: String, classLoader: ClassLoader, methodName: String, vararg parameterTypes: Class<Any>): Result<Method>
    abstract fun hookMethod(method: Method, callback: MethodHookHandler)
    abstract fun findField(classType: Class<*>, fieldName: String): Result<Field>
    abstract fun findClass(className: String, classLoader: ClassLoader): Result<Class<Any>>
    fun findClassesFromStrings(classLoader: ClassLoader, vararg classNames: String): Result<Array<Class<Any>>>
        = runCatching { classNames.map { findClass(it, classLoader).getOrThrow() }.toTypedArray() }
}