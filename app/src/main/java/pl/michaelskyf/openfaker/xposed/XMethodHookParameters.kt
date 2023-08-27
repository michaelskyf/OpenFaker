package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import pl.michaelskyf.openfaker.module.MethodHookParameters
import java.lang.reflect.Method

class XMethodHookParameters(private val param: MethodHookParam)
    : MethodHookParameters(param.method as Method, param.args) {

    override var result: Any?
        get() = param.result
        set(value) { param.result = value }
}