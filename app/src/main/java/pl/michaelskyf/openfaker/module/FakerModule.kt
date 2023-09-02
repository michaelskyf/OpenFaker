package pl.michaelskyf.openfaker.module

import java.util.Optional

abstract class FakerModule(priority: Int): Priority(priority) {

    abstract fun run(hookParameters: HookParameters): Result<Boolean>
    abstract fun getMatchingArgumentsInfo(): Result<MatchingArgumentsInfo>

    abstract inner class FakerArgumentCheckerFunction: Priority(super.priority) {
        abstract fun call(vararg arguments: Any?): Result<Optional<FakerModule>>
    }
}