package pl.michaelskyf.openfaker.module

interface Logger {
    fun log(tag: String, message: String) {
        log("$tag: $message")
    }
    fun log(message: String)
}