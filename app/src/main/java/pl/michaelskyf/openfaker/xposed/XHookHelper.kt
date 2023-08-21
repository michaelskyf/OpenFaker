package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import pl.michaelskyf.openfaker.module.Hook
import pl.michaelskyf.openfaker.module.HookHelper
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Member

class XHookHelper : HookHelper() {

    override fun findMethod(
        className: String,
        classLoader: ClassLoader,
        methodName: String,
        vararg parameterTypes: Any
    ): Member? {

        return XposedHelpers.findMethodExact(className, classLoader, methodName, *parameterTypes)
    }

    override fun hookMethod(member: Member, callback: Hook.MethodHookHandler) {

        XposedBridge.hookMethod(member, XMethodHookHandler(callback))
    }

    override fun findField(classType: Class<*>, fieldName: String): Field? {

        return try {
            XposedHelpers.findField(classType, fieldName)
        } catch (exception: Exception) {
            null
        }
    }
}