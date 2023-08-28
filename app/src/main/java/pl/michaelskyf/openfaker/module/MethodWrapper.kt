package pl.michaelskyf.openfaker.module

import java.lang.reflect.Method

abstract class MethodWrapper(val method: Method) {
    abstract fun invoke(thisObject: Any?, vararg arguments: Any?)

    val declaringClass: Class<*>
        get() = method.declaringClass

    val name: String
        get() = method.name
}