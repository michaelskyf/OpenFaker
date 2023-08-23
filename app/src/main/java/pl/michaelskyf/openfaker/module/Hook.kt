package pl.michaelskyf.openfaker.module

import pl.michaelskyf.openfaker.module.lua.LuaModuleRegistry
import pl.michaelskyf.openfaker.xposed.ClassMethodPair

class Hook(
    private val hookHelper: HookHelper,
    var luaRegistries: Map<ClassMethodPair, LuaModuleRegistry>, // TODO: Convert map to something thread-safe
    private val logger: Logger
    ) {

    fun handleLoadPackage(param: LoadPackageParam) {
        hookMethods(param)
    }

    private fun hookMethods(param: LoadPackageParam) {

    }

    inner class MethodHookHandler {

        fun beforeHookedMethod(hookParameters: MethodHookParameters) {

        }

        fun afterHookedMethod(hookParameters: MethodHookParameters) {

        }
    }
}