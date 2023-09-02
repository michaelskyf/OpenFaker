package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook
import pl.michaelskyf.openfaker.module.HookHandler
import pl.michaelskyf.openfaker.module.HookParameters
import java.lang.reflect.Method

class XHookHandler(private val hookHandler: HookHandler): XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {

        hookHandler.beforeHookedMethod(XHookParameters(param))
    }

    override fun afterHookedMethod(param: MethodHookParam) {

        hookHandler.afterHookedMethod(XHookParameters(param))
    }
}