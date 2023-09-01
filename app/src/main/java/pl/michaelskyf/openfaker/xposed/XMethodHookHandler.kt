package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook
import pl.michaelskyf.openfaker.module.HookHandler

class XMethodHookHandler(private val hookHandler: HookHandler): XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {

        hookHandler.beforeHookedMethod(XMethodHookParameters(param))
    }

    override fun afterHookedMethod(param: MethodHookParam) {

        hookHandler.afterHookedMethod(XMethodHookParameters(param))
    }
}