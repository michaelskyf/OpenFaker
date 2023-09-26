package pl.michaelskyf.openfaker.ui_module_bridge

import kotlinx.serialization.Serializable
import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.Logger

@Serializable
sealed interface FakerModuleFactory {
    fun createFakerModule(logger: Logger): Result<FakerModule>
    fun setUserData(userData: Array<String>)
}