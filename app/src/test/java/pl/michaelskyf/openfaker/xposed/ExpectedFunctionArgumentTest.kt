package pl.michaelskyf.openfaker.xposed

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class ExpectedFunctionArgumentTest {

    @Test
    fun `matches(String, Equal) should return true when comparing two equal objects`(){

        // Assemble
        val expectedString = "Hello"
        val functionString = "Hello"
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.Equal)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }
}