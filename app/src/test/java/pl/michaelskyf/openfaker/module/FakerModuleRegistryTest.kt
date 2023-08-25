package pl.michaelskyf.openfaker.module

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class FakerModuleRegistryTest {

    @Test
    fun `getMatchingModules() should return all matching modules`() {

        val fakerModuleRegistry = FakerModuleRegistry()
        val argument = "Argument"

        val matchingModule = mockk<FakerModule>(relaxed = true)
        val matchingArgumentsInfo = MatchingArgumentsInfo()
        matchingArgumentsInfo.exactMatchArguments(
            FunctionArgument.require("Argument")
        )
        every { matchingModule.getMatchingArgumentsInfo() } returns Result.success(matchingArgumentsInfo)

        val notMatchingModule = mockk<FakerModule>(relaxed = true)
        val notMatchingArgumentsInfo = MatchingArgumentsInfo()
        notMatchingArgumentsInfo.exactMatchArguments(
            FunctionArgument.require("Should never match this string")
        )
        every { notMatchingModule.getMatchingArgumentsInfo() } returns Result.success(notMatchingArgumentsInfo)

        fakerModuleRegistry.register(matchingModule)
        fakerModuleRegistry.register(notMatchingModule)
        fakerModuleRegistry.register(matchingModule)
        fakerModuleRegistry.register(notMatchingModule)

        val matchingModules = fakerModuleRegistry.getMatchingModules(arrayOf(argument))

        var matchingModulesSize = 0
        for (module in matchingModules) {
            if (module !== matchingModule) fail("Got invalid module")
            matchingModulesSize++
        }

        assert(matchingModulesSize == 2)
    }

    @Test
    fun `getMatchingModules() should return all matching modules with custom matching functions`() {

        val fakerModuleRegistry = FakerModuleRegistry()
        val argument = "Argument"

        val matchingModule = mockk<FakerModule>()
        val matchingFunction = mockk<FakerModule.FakerArgumentCheckerFunction>(relaxed = true)
        every { matchingFunction.call(any()) } returns Result.success(matchingModule)
        val matchingArgumentsInfo = MatchingArgumentsInfo()
        matchingArgumentsInfo.customMatchArgument(matchingFunction)

        every { matchingModule.priority } returns 1
        every { matchingModule.getMatchingArgumentsInfo() } returns Result.success(matchingArgumentsInfo)

        val notMatchingModule = mockk<FakerModule>()
        val notMatchingFunction = mockk<FakerModule.FakerArgumentCheckerFunction>(relaxed = true)
        every { notMatchingFunction.call(any()) } returns Result.success(null)
        val notMatchingArgumentsInfo = MatchingArgumentsInfo()
        notMatchingArgumentsInfo.customMatchArgument(notMatchingFunction)

        every { notMatchingModule.priority } returns 1
        every { notMatchingModule.getMatchingArgumentsInfo() } returns Result.success(notMatchingArgumentsInfo)

        fakerModuleRegistry.register(matchingModule)
        fakerModuleRegistry.register(notMatchingModule)
        fakerModuleRegistry.register(matchingModule)
        fakerModuleRegistry.register(notMatchingModule)

        val matchingModules = fakerModuleRegistry.getMatchingModules(arrayOf(argument))

        var matchingModulesSize = 0
        for (module in matchingModules) {
            if (module !== matchingModule) fail("Got invalid module")
            matchingModulesSize++
        }

        assert(matchingModulesSize == 2)
    }
}