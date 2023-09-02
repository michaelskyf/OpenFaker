package pl.michaelskyf.openfaker.module

data class HookParameters (
    val thisObject: Any?,
    val method: MethodWrapper,
    var arguments: Array<Any?>,
    var result: Any?
    ): Cloneable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HookParameters

        if (thisObject != other.thisObject) return false
        if (method != other.method) return false
        if (!arguments.contentEquals(other.arguments)) return false
        if (result != other.result) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = thisObject?.hashCode() ?: 0
        result1 = 31 * result1 + method.hashCode()
        result1 = 31 * result1 + arguments.contentHashCode()
        result1 = 31 * result1 + (result?.hashCode() ?: 0)
        return result1
    }

    public override fun clone(): Any
        = super.clone()
}