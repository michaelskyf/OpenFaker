package pl.michaelskyf.openfaker.module

import java.lang.reflect.Field
import java.lang.reflect.Method

interface HookHelper {
    fun findMethod(className: String, classLoader: ClassLoader, methodName: String, vararg parameterTypes: Class<*>): Result<Method>
    fun findMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Result<Method>
    fun hookMethod(method: Method, callback: HookHandler)
    fun findField(classType: Class<*>, fieldName: String): Result<Field>
    fun findClass(className: String, classLoader: ClassLoader): Result<Class<*>>
}