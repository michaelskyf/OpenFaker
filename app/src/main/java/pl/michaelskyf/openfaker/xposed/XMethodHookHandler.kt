package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook
import pl.michaelskyf.openfaker.module.Hook

class XMethodHookHandler(private val methodHookHandler: Hook.MethodHookHandler): XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {

        methodHookHandler.beforeHookedMethod(XMethodHookParameters(param))
    }

    override fun afterHookedMethod(param: MethodHookParam) {

        methodHookHandler.afterHookedMethod(XMethodHookParameters(param))
    }
}