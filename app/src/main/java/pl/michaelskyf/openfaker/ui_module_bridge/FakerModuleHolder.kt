package pl.michaelskyf.openfaker.ui_module_bridge

import pl.michaelskyf.openfaker.module.FakerModule
import pl.michaelskyf.openfaker.module.Logger

abstract class FakerModuleHolder(val priority: Int) {
    abstract fun toFakerModule(logger: Logger): Result<FakerModule>
}