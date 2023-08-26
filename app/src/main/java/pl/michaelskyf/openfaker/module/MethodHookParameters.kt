package pl.michaelskyf.openfaker.module

import java.lang.reflect.Method

abstract class MethodHookParameters(
    val method: Method,
    val arguments: Array<*>
    ) {
    abstract var result: Any?
}