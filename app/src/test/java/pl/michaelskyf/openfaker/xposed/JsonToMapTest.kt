package pl.michaelskyf.openfaker.xposed

import com.google.gson.Gson
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

class JsonToMapTest {

    @Test
    fun `getMapFromJson() from MethodArguments`() {

        // Setup
        val className = "some.class"
        val methodName = "someMethod"
        val fakeValue = "Fake value"
        val typeValuePairArray: Array<Pair<String, Any?>> = arrayOf( Pair(String::class.java.typeName, "Hello"), Pair(Integer::class.javaObjectType.typeName, 1337.0) )

        val methodArguments = JsonToMap.MethodArguments(className, methodName, fakeValue, typeValuePairArray)
        val methodArgumentsArray = arrayOf(methodArguments)

        val json = Gson().toJson(methodArgumentsArray)

        // Run
        val map = JsonToMap().getMapFromJson(json) ?: fail("Failed to get map from json")

        //Check
        val returnedMethodArguments = map[Pair(className, methodName)] ?: fail("Map doesn't contain the value")
        val mappedArguments = returnedMethodArguments.second.map { Pair(it.first.typeName, it.second) }.toTypedArray()
        assert(returnedMethodArguments.first == methodArguments.fakeValue)
        assert(mappedArguments.contentEquals(methodArguments.typeValuePairArray))
    }
}