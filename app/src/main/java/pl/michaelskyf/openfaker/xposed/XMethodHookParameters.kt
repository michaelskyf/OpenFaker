package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import pl.michaelskyf.openfaker.module.MethodHookParameters

class XMethodHookParameters(private val param: MethodHookParam)
    : MethodHookParameters(param.method, param.args) {

    override var result: Any?
        get() = param.result
        set(value) { param.result = value }
}