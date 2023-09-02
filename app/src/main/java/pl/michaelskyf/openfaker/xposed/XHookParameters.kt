package pl.michaelskyf.openfaker.xposed

import pl.michaelskyf.openfaker.module.HookParameters

import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import java.lang.reflect.Method

class XHookParameters(private val param: MethodHookParam)
    : HookParameters(param.thisObject, XMethodWrapper(param.method as Method)) {

    override var arguments: Array<Any?>
        get() = param.args
        set(value) { param.args = value }
    override var result: Any?
        get() = param.result
        set(value) { param.result = value }
}