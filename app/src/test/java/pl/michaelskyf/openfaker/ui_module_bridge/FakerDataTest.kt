package pl.michaelskyf.openfaker.ui_module_bridge

import android.media.MediaPlayer
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pl.michaelskyf.openfaker.lua.LuaFakerModuleFactory
import java.lang.reflect.Type

class FakerDataTest {
    @Test
    fun temporary() {
        val lua = """
            function registerModule(moduleRegistry)
                moduleRegistry:exactMatchArguments({argument:ignore()})
            end
            
            function runModule(hookParameters)
                local arguments = hookParameters:getArguments()
                local obj = hookParameters:getThisObject()
                local method = hookParameters:getMethod()
                
                local result = method:invoke(obj, {"/sdcard/Music/WakacyjnaMilosc.opus"})
                logger:log("Hi")
                hookParameters:setResult(result)
                
                return true
            end
        """.trimIndent()
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(FakerModuleFactory::class.java,
            PropertyBasedInterfaceMarshal()
        )
        val gson = gsonBuilder.create()

        val json = gson.toJson(arrayOf(
            MethodHookHolder(
                MediaPlayer::class.java.name,
                "setDataSource",
                arrayOf(String::class.java.name),
                LuaFakerModuleFactory(lua, 0),
                MethodHookHolder.WhenToHook.Before
            )
        ))

        println(json)
        val value = gson.fromJson(json, Array<MethodHookHolder>::class.java)!!
    }

    private class PropertyBasedInterfaceMarshal : JsonSerializer<Any?>,
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
}