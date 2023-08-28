package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import pl.michaelskyf.openfaker.module.HookHelper
import pl.michaelskyf.openfaker.module.MethodHookHandler
import java.lang.reflect.Field
import java.lang.reflect.Method

class XHookHelper : HookHelper() {

    override fun findMethod(
        className: String,
        classLoader: ClassLoader,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Result<Method>
        = runCatching { XposedHelpers.findMethodExact(className, classLoader, methodName, *parameterTypes) }

    override fun findMethod(
        clazz: Class<*>,
        methodName: String,
        vararg parameterTypes: Class<*>
    ): Result<Method>
            = runCatching { XposedHelpers.findMethodExact(clazz, methodName, *parameterTypes) }

    override fun hookMethod(method: Method, callback: MethodHookHandler) {
        XposedBridge.hookMethod(method, XMethodHookHandler(callback))
    }

    override fun findField(classType: Class<*>, fieldName: String): Result<Field>
        = runCatching { XposedHelpers.findField(classType, fieldName) }

    override fun findClass(className: String, classLoader: ClassLoader): Result<Class<*>>
        = runCatching { XposedHelpers.findClass(className, classLoader) }
}