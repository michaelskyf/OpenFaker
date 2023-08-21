package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.module.Hook
import java.lang.reflect.Field
import java.lang.reflect.Member

abstract class HookHelper {
    open fun findMethod(className: String, classLoader: ClassLoader, methodName: String, vararg parameterTypes: Any): Member? {

        return null
    }

    open fun hookMethod(member: Member, callback: Hook.MethodHookHandler) {

    }

    open fun findField(classType: Class<*>, fieldName: String): Field? {

        return null
    }
}