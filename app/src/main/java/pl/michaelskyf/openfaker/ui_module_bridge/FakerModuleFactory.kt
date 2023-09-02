package pl.michaelskyf.openfaker.ui_module_bridge

import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.Logger

interface FakerModuleFactory {
    fun createFakerModule(logger: Logger): Result<FakerModule>
}