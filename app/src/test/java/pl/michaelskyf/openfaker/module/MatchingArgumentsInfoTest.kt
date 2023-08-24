package pl.michaelskyf.openfaker.module

import io.mockk.mockk
import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.module.lua.FunctionArgument
import pl.michaelskyf.openfaker.module.lua.MatchingArgumentsInfo

class MatchingArgumentsInfoTest {
    @Test
    fun `exactMatchArguments() should add given arguments only to the correct list`() {
        val matchingArgumentsInfo = MatchingArgumentsInfo()
        val arguments = arrayOf(
            FunctionArgument.ignore(),
            FunctionArgument.require("Hello"),
            FunctionArgument.require("World"),
            FunctionArgument.require(FunctionArgument.ignore())
        )
        matchingArgumentsInfo.exactMatchArguments(*arguments)

        assert(matchingArgumentsInfo.exactMatchArguments.size == 1)
        assert(matchingArgumentsInfo.exactMatchArguments.first().contentEquals(arguments))
        assert(matchingArgumentsInfo.customArgumentMatchingFunctions.isEmpty())
    }

    @Test
    fun `customMatchArgument() should add custom comparison function only to the correct list`() {
        val matchingInfo = MatchingArgumentsInfo()
        val customFunction = mockk<FakerArgumentCheckerFunction>(relaxed = true)
        matchingInfo.customMatchArgument(customFunction)

        assert(matchingInfo.customArgumentMatchingFunctions.size == 1)
        assert(matchingInfo.customArgumentMatchingFunctions.first() == customFunction)
        assert(matchingInfo.exactMatchArguments.isEmpty())
    }
}