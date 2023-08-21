package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.BuildConfig

abstract class Logger {

    abstract fun log(tag: String, message: String)
    abstract fun log(message: String)
}