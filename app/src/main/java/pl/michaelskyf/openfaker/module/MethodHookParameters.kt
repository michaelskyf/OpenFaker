package pl.michaelskyf.openfaker.module

import java.lang.reflect.Method

abstract class MethodHookParameters(val method: Method): Cloneable {
    abstract var arguments: Array<out Any?>
    abstract var result: Any?

    public override fun clone(): Any {
        return super.clone()
    }
}