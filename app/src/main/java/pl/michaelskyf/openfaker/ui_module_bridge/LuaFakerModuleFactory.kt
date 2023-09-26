package pl.michaelskyf.openfaker.ui_module_bridge

import kotlinx.serialization.Serializable
import pl.michaelskyf.openfaker.lua.LuaFakerModule
import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.Logger

@Serializable
class LuaFakerModuleFactory(private val luaScript: String, private val userData: Array<String>?, private val priority: Int): FakerModuleFactory {
    override fun createFakerModule(logger: Logger): Result<FakerModule>
        = LuaFakerModule(luaScript, userData, priority, logger)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LuaFakerModuleFactory

        if (luaScript != other.luaScript) return false
        if (userData != null) {
            if (other.userData == null) return false
            if (!userData.contentEquals(other.userData)) return false
        } else if (other.userData != null) return false
        if (priority != other.priority) return false

        return true
    }

    override fun hashCode(): Int {
        var result = luaScript.hashCode()
        result = 31 * result + (userData?.contentHashCode() ?: 0)
        result = 31 * result + priority
        return result
    }
}