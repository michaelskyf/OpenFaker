package pl.michaelskyf.openfaker.module
class FunctionArgument private constructor(val value: Any?, val shouldIgnore: Boolean = false) {

    companion object {
        @JvmStatic
        fun ignore(): FunctionArgument {
            return FunctionArgument(null, true)
        }

        @JvmStatic
        fun require(value: Any?): FunctionArgument {
            return FunctionArgument(value, false)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FunctionArgument) return false

        return value == other.value
                && shouldIgnore == other.shouldIgnore
    }

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + shouldIgnore.hashCode()
        return result
    }
}