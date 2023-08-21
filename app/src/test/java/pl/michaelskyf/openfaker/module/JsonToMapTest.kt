package pl.michaelskyf.openfaker.module

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class JsonToMapTest {

    @Test
    fun `getMapFromJson() should return the same MethodArguments after conversion to json`() {

        // Assemble
        val className = "some.class"
        val methodName = "someMethod"
        val fakeValue = "Fake value"
        val expectedArguments: Array<ExpectedFunctionArgument> = arrayOf( ExpectedFunctionArgument("Hello"),
            ExpectedFunctionArgument(1337), ExpectedFunctionArgument(String::class.java,null),
            ExpectedFunctionArgument(13.37), ExpectedFunctionArgument(1337.0))

        val methodArguments = MethodArguments(className, methodName, fakeValue, expectedArguments)
        val methodArgumentsArray = arrayOf(methodArguments)

        val json = Gson().toJson(methodArgumentsArray)

        // Run
        val map = JsonToMap().getMapFromJson(json)
            ?: fail("Failed to get map from json")

        // Assert
        val returnedMethodArguments = map[Pair(className, methodName)]
            ?: fail("Map doesn't contain the value")
        val mappedArguments = returnedMethodArguments.second

        assert(returnedMethodArguments.first == methodArguments.fakeValue)
        assert(mappedArguments.contentEquals(expectedArguments))
    }

    @Test
    fun `getMapFromJson() should return null if it cannot find a class`() {

        // Assemble
        val nonExistantClassName = "this.class.should.never.ever.exist.in.this.project"

        val json = """
            [{
                "className":"some.class",
                "methodName":"someMethod",
                "fakeValue":"Fake value",
                "typeValuePairArray":[
                    {"first":"$nonExistantClassName","second":"Hello","third":"Equal"},
                    {"first":"java.lang.Integer","second":1337,"third":"Equal"},
                    {"first":"java.lang.String","third":"Equal"},
                    {"first":"java.lang.Double","second":13.37,"third":"Equal"},
                    {"first":"java.lang.Double","second":1337.0,"third":"Equal"}
                ]
            }]""".trimMargin()

        // Run
        val map = JsonToMap().getMapFromJson(json)

        // Assert
        assert(map == null)
    }
}