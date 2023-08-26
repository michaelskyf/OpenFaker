package pl.michaelskyf.openfaker.module

import java.lang.reflect.Field
import java.lang.reflect.Method

abstract class HookHelper {
    open fun findMethod(className: String, classLoader: ClassLoader, methodName: String, vararg parameterTypes: Any): Method? = null
    open fun hookMethod(method: Method, callback: Hook.MethodHookHandler) = Unit
    open fun findField(classType: Class<*>, fieldName: String): Field? = null
}