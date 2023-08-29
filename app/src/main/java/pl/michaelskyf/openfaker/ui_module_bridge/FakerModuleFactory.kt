package pl.michaelskyf.openfaker.ui_module_bridge

import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.Logger

abstract class FakerModuleFactory(val priority: Int) {
    abstract fun createFakerModule(logger: Logger): Result<FakerModule>
}