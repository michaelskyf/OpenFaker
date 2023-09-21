package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XSharedPreferences
import kotlinx.serialization.json.Json
import pl.michaelskyf.openfaker.ui_module_bridge.DataTunnel
import pl.michaelskyf.openfaker.ui_module_bridge.HookData
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData

class XSharedPreferencesDataTunnel(private val prefs: XSharedPreferences): DataTunnel {
    private var modifiedKeys: HashSet<String> = hashSetOf()
    override fun get(className: String, methodName: String): Result<MethodData> = runCatching {
        val key = "$className.$methodName"
        val json = prefs.getString(key, null)
            ?: throw Exception("Failed to get json from $key")

        modifiedKeys.remove(key)
        Json.decodeFromString(json)
    }

    override fun hasHookChanged(className: String, methodName: String): Boolean {
        val key = "$className.$methodName"
        reload()

        return modifiedKeys.contains(key)
    }

    override fun getAllHooks(): Result<List<MethodData>> = runCatching {
        modifiedKeys.clear()

        val rawData = prefs.all.filterKeys { it != "modifiedKeys" } as Map<String, String>
        val classHookerDataArray = rawData.map { Json.decodeFromString<MethodData>(it.value) }

        classHookerDataArray
    }

    override fun reload(): Boolean {
        if (!prefs.hasFileChanged()) return false

        prefs.reload()

        val json = prefs.getString("modifiedKeys", null) ?: return false
        val newModifiedKeys = Json.decodeFromString<Array<String>>(json)

        modifiedKeys.addAll(newModifiedKeys)

        return true
    }
}