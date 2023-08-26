package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import pl.michaelskyf.openfaker.module.MethodHookParameters
import java.lang.reflect.Method

class XMethodHookParameters(private val param: MethodHookParam)
    : MethodHookParameters(param.method as Method) {

    override var arguments: Array<out Any?>
        get() = param.args
        set(value) { param.args = value }

    override var result: Any?
        get() = param.result
        set(value) { param.result = value }
}