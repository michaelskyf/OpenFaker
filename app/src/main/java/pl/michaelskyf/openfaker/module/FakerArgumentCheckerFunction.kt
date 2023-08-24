package pl.michaelskyf.openfaker.module

abstract class FakerArgumentCheckerFunction {

    abstract fun call(vararg arguments: Any?): Result<FakerModule?>
}