package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.module.lua.MatchingArgumentsInfo

abstract class FakerModule(priority: Int): Priority(priority) {

    abstract fun run(hookParameters: MethodHookParameters): Result<Boolean>
    abstract fun getMatchingArgumentsInfo(): MatchingArgumentsInfo

    abstract inner class FakerArgumentCheckerFunction: Priority(super.priority) {

        abstract fun callImpl(vararg arguments: Any?): Boolean
        fun call(vararg arguments: Any?): Result<FakerModule?> {
            val result = try {
                callImpl(arguments)
            } catch (exception: Exception) {
                return Result.failure(exception)
            }

            return when (result) {
                true -> Result.success(this@FakerModule)
                false -> Result.success(null)
            }
        }
    }
}