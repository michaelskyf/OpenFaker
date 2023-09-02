package pl.michaelskyf.openfaker

import pl.michaelskyf.openfaker.module.Logger

class TestLogger: Logger {
    override fun log(tag: String, message: String) {
        log("$tag: $message")
    }

    override fun log(message: String) {
        println(message)
    }
}