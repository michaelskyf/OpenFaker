package pl.michaelskyf.openfaker.ui_module_bridge

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

// https://stackoverflow.com/questions/3629596/deserializing-an-abstract-class-in-gson Thank you very much! :)
class PropertyBasedInterfaceMarshal : JsonSerializer<Any?>,
    JsonDeserializer<Any?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        jsonElement: JsonElement, type: Type?,
        jsonDeserializationContext: JsonDeserializationContext
    ): Any {
        val jsonObj = jsonElement.asJsonObject
        val className = jsonObj[CLASS_META_KEY].asString
        return try {
            val clz = Class.forName(className)
            jsonDeserializationContext.deserialize<Any>(jsonElement, clz)
        } catch (e: ClassNotFoundException) {
            throw JsonParseException(e)
        }
    }

    override fun serialize(
        src: Any?,
        type: Type?,
        jsonSerializationContext: JsonSerializationContext?
    ): JsonElement? {
        val jsonEle = jsonSerializationContext!!.serialize(src, src!!.javaClass)
        jsonEle.asJsonObject.addProperty(
            CLASS_META_KEY,
            src.javaClass.canonicalName
        )
        return jsonEle
    }

    companion object {
        private const val CLASS_META_KEY = "CLASS_META_KEY"
    }
}