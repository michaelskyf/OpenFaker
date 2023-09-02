package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XC_MethodHook
import pl.michaelskyf.openfaker.module.HookHandler
import pl.michaelskyf.openfaker.module.HookParameters
import java.lang.reflect.Method

class XMethodHookHandler(private val hookHandler: HookHandler): XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {

        val newParams = convertParameters(param)
        val skipMethodCall = hookHandler.beforeHookedMethod(newParams)
        if(!skipMethodCall) {
            param.args = newParams.arguments
            param.result = newParams.result
        }
    }

    override fun afterHookedMethod(param: MethodHookParam) {

        hookHandler.afterHookedMethod(convertParameters(param))
    }

    private fun convertParameters(param: MethodHookParam): HookParameters
        = HookParameters(param.thisObject, XMethodWrapper(param.method as Method), param.args, param.result)
}