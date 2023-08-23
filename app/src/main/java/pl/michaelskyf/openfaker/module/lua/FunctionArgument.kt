package pl.michaelskyf.openfaker.module.lua
class FunctionArgument(val value: Any?, val shouldIgnore: Boolean = false) {

    companion object {
        @JvmStatic
        fun ignore(): FunctionArgument {
            return FunctionArgument(null, true)
        }

        @JvmStatic
        fun require(value: Any?): FunctionArgument {
            return FunctionArgument(value, true)
        }
    }
}