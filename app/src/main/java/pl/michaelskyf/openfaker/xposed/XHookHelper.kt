package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
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
}