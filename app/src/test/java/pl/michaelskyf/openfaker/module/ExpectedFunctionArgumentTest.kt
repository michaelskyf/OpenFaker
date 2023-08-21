package pl.michaelskyf.openfaker.module

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pl.michaelskyf.openfaker.module.ExpectedFunctionArgument

class ExpectedFunctionArgumentTest {

    @Test
    fun `constructor should throw an exception when type and value types differ`() {
        try {
            ExpectedFunctionArgument(String::class.java, 15)
            fail("Exception wasn't thrown")
        } catch (_: Exception) {}
    }

    @Test
    fun `constructor shouldn't throw an exception when type and value types differ and value is null`() {
        try {
            ExpectedFunctionArgument(String::class.java, null)
        } catch (_: Exception) {
            fail("Exception was thrown")
        }
    }

    @Test
    fun `constructor shouldn't throw an exception when type and value types differ and compare operation is AlwaysTrue`() {
        try {
            ExpectedFunctionArgument(String::class.java, 15, ExpectedFunctionArgument.CompareOperation.AlwaysTrue)
        } catch (_: Exception) {
            fail("Exception was thrown")
        }
    }

    @Test
    fun `matches(String, Equal) should return true when comparing two equal objects`() {

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