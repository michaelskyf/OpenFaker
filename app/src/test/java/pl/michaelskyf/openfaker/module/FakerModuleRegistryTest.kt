package pl.michaelskyf.openfaker.module

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pl.michaelskyf.openfaker.module.lua.FunctionArgument
import pl.michaelskyf.openfaker.module.lua.MatchingArgumentsInfo

class FakerModuleRegistryTest {

    @Test
    fun `getMatchingModules() should return all matching FakerModules`() {

        val fakerModuleRegistry = FakerModuleRegistry()
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

        val matchingModules = fakerModuleRegistry.getMatchingModules(arrayOf(argument))

        for (module in matchingModules) {
            if (module !== matchingModule) fail("Got invalid module")
        }
    }
}