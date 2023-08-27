package pl.michaelskyf.openfaker.module

import java.lang.reflect.Method

abstract class MethodHookParameters(val method: Method, var arguments: Array<Any?>): Cloneable {
    abstract var result: Any?

    public override fun clone(): Any {
        return super.clone()
    }
}