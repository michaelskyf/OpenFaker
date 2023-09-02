package pl.michaelskyf.openfaker.module

abstract class HookParameters(
    open val thisObject: Any?,
    open val method: MethodWrapper
): Cloneable {
    abstract var arguments: Array<Any?>
    abstract var result: Any?

    public override fun clone(): Any {
        return super.clone()
    }
}