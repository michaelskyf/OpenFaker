package pl.michaelskyf.openfaker.module

import java.lang.reflect.Method

abstract class MethodHookParameters(
    open val thisObject: Any?,
    open val method: MethodWrapper,
    open val logger: Logger
    ) {
    abstract var arguments: Array<Any?>
    abstract var result: Any?
}