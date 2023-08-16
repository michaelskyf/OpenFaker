package pl.michaelskyf.openfaker.xposed

import java.lang.reflect.Member

abstract class HookHelper {
    open fun findMethod(className: String, classLoader: ClassLoader, methodName: String, vararg parameterTypes: Class<*>): Member? {

        return null
    }

    open fun hookMethod(member: Member, callback: Hook.MethodHookHandler) {

    }
}