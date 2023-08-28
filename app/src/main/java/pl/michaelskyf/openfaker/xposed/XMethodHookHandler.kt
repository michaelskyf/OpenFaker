package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook
import pl.michaelskyf.openfaker.module.Logger
import pl.michaelskyf.openfaker.module.MethodHookHandler

class XMethodHookHandler(private val methodHookHandler: MethodHookHandler): XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {

        methodHookHandler.beforeHookedMethod(XMethodHookParameters(param))
    }

    override fun afterHookedMethod(param: MethodHookParam) {

        methodHookHandler.afterHookedMethod(XMethodHookParameters(param))
    }
}