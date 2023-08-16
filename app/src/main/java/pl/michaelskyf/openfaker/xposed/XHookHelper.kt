package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XposedHelpers

class XHookHelper : HookHelper() {

    override fun findAndHookMethod(
        className: String,
        classLoader: ClassLoader,
        methodName: String,
        callback: Hook.MethodHookHandler,
        vararg parameterTypes: Class<*>
    ) {

        val xMethodHookHandler = XMethodHookHandler(callback)

        XposedHelpers.findAndHookMethod(className, classLoader, methodName, *parameterTypes, xMethodHookHandler)
    }
}