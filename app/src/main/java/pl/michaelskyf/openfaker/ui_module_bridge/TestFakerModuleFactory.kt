package pl.michaelskyf.openfaker.ui_module_bridge

import kotlinx.serialization.Serializable
import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.Logger

@Serializable
class TestFakerModuleFactory : FakerModuleFactory {
    override fun createFakerModule(logger: Logger): Result<FakerModule> {
        TODO("Not yet implemented")
    }

    override fun setUserData(userData: Array<String>) {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}