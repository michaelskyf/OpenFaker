package pl.michaelskyf.openfaker.module

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.module.ExpectedFunctionArgument
import pl.michaelskyf.openfaker.module.JsonToMap

class JsonToMapTest {

    @Test
    fun `getMapFromJson() from MethodArguments`() {

        // Assemble
        val className = "some.class"
        val methodName = "someMethod"
        val fakeValue = "Fake value"
        val typeValuePairArray: Array<ExpectedFunctionArgument> = arrayOf( ExpectedFunctionArgument("Hello"),
            ExpectedFunctionArgument(1337) )

        val methodArguments = JsonToMap.MethodArguments(className, methodName, fakeValue, typeValuePairArray)
        val methodArgumentsArray = arrayOf(methodArguments)

        val json = Gson().toJson(methodArgumentsArray)

        // Run
        val map = JsonToMap().getMapFromJson(json) ?: fail("Failed to get map from json")

        // Assert
        val returnedMethodArguments = map[Pair(className, methodName)] ?: fail("Map doesn't contain the value")
        val mappedArguments = returnedMethodArguments.second
        assert(returnedMethodArguments.first == methodArguments.fakeValue)
        assert(mappedArguments.contentEquals(typeValuePairArray))
    }
}