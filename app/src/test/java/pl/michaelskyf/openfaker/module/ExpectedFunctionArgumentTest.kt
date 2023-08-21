package pl.michaelskyf.openfaker.module

import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.module.ExpectedFunctionArgument

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

    @Test
    fun `matches(String, Equal) should return false when comparing two unequal objects`(){

        // Assemble
        val expectedString = "Hello"
        val functionString = "Hello2"
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.Equal)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, Unequal) should return true when comparing two unequal objects`(){

        // Assemble
        val expectedString = "Hello"
        val functionString = "Hello2"
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.Unequal)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }

    @Test
    fun `matches(String, Unequal) should return false when comparing two equal objects`(){

        // Assemble
        val expectedString = "Hello"
        val functionString = "Hello"
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.Unequal)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, LessThanExpected) should return true when function value is less than expected value`(){

        // Assemble
        val expectedString = "HelloLonger"
        val functionString = "Hello"
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }

    @Test
    fun `matches(String, LessThanExpected) should return false when strings are equal`(){

        // Assemble
        val expectedString = "Hello"
        val functionString = "Hello"
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, LessThanExpected) should return false when function value is greater than expected value`(){

        // Assemble
        val expectedString = "Hello"
        val functionString = "HelloLonger"
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, LessThanExpected) should return false when types differ`(){

        // Assemble
        val expectedString = "Hello"
        val functionInt = 20
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionInt)

        // Assert
        assert(!result)
    }
}