package pl.michaelskyf.openfaker.module

import de.robv.android.xposed.XposedHelpers
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method

abstract class HookHelper {
    abstract fun findMethod(className: String, classLoader: ClassLoader, methodName: String, vararg parameterTypes: Any): Result<Method>
    abstract fun hookMethod(method: Method, callback: MethodHookHandler)
    abstract fun findField(classType: Class<*>, fieldName: String): Result<Field>
    abstract fun findClass(className: String, classLoader: ClassLoader): Result<Class<*>>
}