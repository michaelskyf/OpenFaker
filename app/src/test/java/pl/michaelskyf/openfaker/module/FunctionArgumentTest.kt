package pl.michaelskyf.openfaker.module

import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.module.FunctionArgument

class FunctionArgumentTest {

    @Test
    fun `ignore() should set the ignore parameter to true`() {
        val functionArgument = FunctionArgument.ignore()

        assert(functionArgument.shouldIgnore)
    }

    @Test
    fun `require() should set the value and should set the ignore parameter to false`() {
        val functionArgument = FunctionArgument.require("Hello, World!")

        assert(functionArgument.value == "Hello, World!")
        assert(!functionArgument.shouldIgnore)
    }
}