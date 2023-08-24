package pl.michaelskyf.openfaker.module

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pl.michaelskyf.openfaker.module.lua.ArgumentMatcher
import pl.michaelskyf.openfaker.module.lua.FunctionArgument
import pl.michaelskyf.openfaker.module.lua.MatchingArgumentsInfo

class FakerModuleRegistryTest {

    @Test
    fun `forEachMatchingModule() should pass all matching FakerModules`() {

        val fakerModuleRegistry = FakerModuleRegistry()
        val receivedMatchingModules = mutableListOf<FakerModule>()
        val argument = "Argument"

        val matchingModule = mockk<FakerModule>(relaxed = true)
        val matchingArgumentsInfo = MatchingArgumentsInfo()
        matchingArgumentsInfo.exactMatchArguments(
            FunctionArgument.require("Argument")
        )
        every { matchingModule.getMatchingArgumentsInfo() } returns matchingArgumentsInfo

        val notMatchingModule = mockk<FakerModule>(relaxed = true)
        val notMatchingArgumentsInfo = MatchingArgumentsInfo()
        notMatchingArgumentsInfo.exactMatchArguments(
            FunctionArgument.require("Should never match this string")
        )
        every { notMatchingModule.getMatchingArgumentsInfo() } returns notMatchingArgumentsInfo

        fakerModuleRegistry.register(matchingModule)
        fakerModuleRegistry.register(notMatchingModule)
        fakerModuleRegistry.register(matchingModule)
        fakerModuleRegistry.register(notMatchingModule)

        fakerModuleRegistry.forEachMatchingModule(arrayOf(argument)) {
            receivedMatchingModules.add(this)
        }

        assert(receivedMatchingModules.isNotEmpty())
        receivedMatchingModules.forEach {
            if (it !== matchingModule) fail("Not matching module found!")
        }
    }
}