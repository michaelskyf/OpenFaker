package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook

class XMethodHookHandler(private val methodHookHandler: Hook.MethodHookHandler): XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {

        methodHookHandler.beforeHookedMethod(param)
    }
}