package pl.michaelskyf.openfaker.module

import java.lang.reflect.Member

abstract class MethodHookParameters(
    val method: Member,
    val arguments: Array<*>
    ) {
    abstract var result: Any?
}