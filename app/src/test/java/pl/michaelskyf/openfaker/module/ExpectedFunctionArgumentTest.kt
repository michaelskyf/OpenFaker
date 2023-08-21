package pl.michaelskyf.openfaker.module

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ExpectedFunctionArgumentTest {

    private val HELLO_STRING = "Hello"
    private val LONG_HELLO_STRING = "Long Hello!"

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
    fun `objects with the same type but different value should have different hash and should not be equal`() {
        val first = ExpectedFunctionArgument(String::class.java, "Test")
        val second = ExpectedFunctionArgument(String::class.java, "Test2")

        assert(first != second)
        assert(first.hashCode() != second.hashCode())
    }

    @Test
    fun `objects with the same value but different types should have different hash and should not be equal`() {
        val first = ExpectedFunctionArgument(String::class.java, null)
        val second = ExpectedFunctionArgument(Integer::class.java, null)

        assert(first != second)
        assert(first.hashCode() != second.hashCode())
    }

    @Test
    fun `objects with the same value and same types should have the same hash and should be equal`() {
        val first = ExpectedFunctionArgument(String::class.java, "Hello")
        val second = ExpectedFunctionArgument(String::class.java, "Hello")

        assert(first == second)
        assert(first.hashCode() == second.hashCode())
    }

    @Test
    fun `objects with the same value and same types, but different compare operator should have different hash and should not be equal`() {
        val first = ExpectedFunctionArgument(String::class.java, "Hello", ExpectedFunctionArgument.CompareOperation.Equal)
        val second = ExpectedFunctionArgument(String::class.java, "Hello", ExpectedFunctionArgument.CompareOperation.AlwaysTrue)

        assert(first != second)
        assert(first.hashCode() != second.hashCode())
    }

    @Test
    fun `matches(String, Equal) should return true when comparing two equal objects`() {

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.Equal)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }

    @Test
    fun `matches(String, Equal) should return false when comparing two unequal objects`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = LONG_HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.Equal)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, Unequal) should return true when comparing two unequal objects`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = LONG_HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.Unequal)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }

    @Test
    fun `matches(String, Unequal) should return false when comparing two equal objects`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.Unequal)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, LessThanExpected) should return true when function value is less than expected value`(){

        // Assemble
        val expectedString = LONG_HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }

    @Test
    fun `matches(String, LessThanExpected) should return false when strings are equal`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, LessThanExpected) should return false when function value is greater than the expected value`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = LONG_HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, LessThanExpected) should return false when types differ`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionInt = 20
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionInt)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, LessEqualExpected) should return true when function value is smaller than the expected value`(){

        // Assemble
        val expectedString = LONG_HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessEqualExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }

    @Test
    fun `matches(String, LessEqualExpected) should return true when function value is equal the expected value`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessEqualExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }

    @Test
    fun `matches(String, LessEqualExpected) should return false when function value is greater than the expected value`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = LONG_HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.LessEqualExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, GreaterThanExpected) should return true when function value is greater than the expected value`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = LONG_HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.GreaterThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }

    @Test
    fun `matches(String, GreaterThanExpected) should return false when function value is equal the expected value`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.GreaterThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, GreaterThanExpected) should return false when function value is smaller than the expected value`(){

        // Assemble
        val expectedString = LONG_HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.GreaterThanExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, GreaterEqualExpected) should return true when function value is greater than the expected value`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = LONG_HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.GreaterEqualExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }

    @Test
    fun `matches(String, GreaterEqualExpected) should return true when function value is equal the expected value`(){

        // Assemble
        val expectedString = HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.GreaterEqualExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }

    @Test
    fun `matches(String, GreaterEqualExpected) should return false when function value is less than the expected value`(){

        // Assemble
        val expectedString = LONG_HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.GreaterEqualExpected)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(!result)
    }

    @Test
    fun `matches(String, AlwaysTrue) should always return true`(){

        // Assemble
        val expectedString = LONG_HELLO_STRING
        val functionString = HELLO_STRING
        val expectedFunctionArgument = ExpectedFunctionArgument(expectedString, ExpectedFunctionArgument.CompareOperation.AlwaysTrue)

        // Run
        val result = expectedFunctionArgument.matches(functionString)

        // Assert
        assert(result)
    }
}