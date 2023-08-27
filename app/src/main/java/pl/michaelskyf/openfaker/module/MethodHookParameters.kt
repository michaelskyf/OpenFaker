package pl.michaelskyf.openfaker.module

import java.lang.reflect.Method

abstract class MethodHookParameters(open val method: Method) {
    abstract var arguments: Array<Any?>
    abstract var result: Any?
}