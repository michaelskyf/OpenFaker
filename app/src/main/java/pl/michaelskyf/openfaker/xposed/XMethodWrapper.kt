package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XposedBridge
import pl.michaelskyf.openfaker.module.MethodWrapper
import java.lang.reflect.Method

class XMethodWrapper(method: Method): MethodWrapper(method) {
    override fun invoke(thisObject: Any?, vararg arguments: Any?) {
        XposedBridge.invokeOriginalMethod(method, thisObject, arguments)
    }
}