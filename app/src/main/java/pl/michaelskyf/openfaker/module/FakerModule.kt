package pl.michaelskyf.openfaker.module

abstract class FakerModule(priority: Int): Priority(priority) {

    abstract fun run(hookParameters: MethodHookParameters): Result<Boolean>
    abstract fun getMatchingArgumentsInfo(): Result<MatchingArgumentsInfo>

    abstract inner class FakerArgumentCheckerFunction: Priority(super.priority) {
        abstract fun call(vararg arguments: Any?): Result<FakerModule?>
    }
}