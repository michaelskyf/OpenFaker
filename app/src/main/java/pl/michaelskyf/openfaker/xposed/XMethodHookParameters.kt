package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import pl.michaelskyf.openfaker.module.Logger
import pl.michaelskyf.openfaker.module.MethodHookParameters
import pl.michaelskyf.openfaker.module.MethodWrapper
import java.lang.reflect.Method

class XMethodHookParameters(private val param: MethodHookParam, logger: Logger)
    : MethodHookParameters(param.thisObject, XMethodWrapper(param.method as Method), logger = logger) {

    override var arguments: Array<Any?>
        get() = param.args
        set(value) { param.args = value }
    override var result: Any?
        get() = param.result
        set(value) { param.result = value }
}