package pl.michaelskyf.openfaker.module

abstract class MethodHookHandler {
    open fun beforeHookedMethod(hookParameters: MethodHookParameters) = Unit

    open fun afterHookedMethod(hookParameters: MethodHookParameters) = Unit
}