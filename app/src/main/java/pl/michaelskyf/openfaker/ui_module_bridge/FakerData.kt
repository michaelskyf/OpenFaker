package pl.michaelskyf.openfaker.ui_module_bridge

import java.util.PriorityQueue

abstract class FakerData {
    companion object {
        val fakerDataFileName = "faker_data_shared_preferences"
    }

    val methodHooksKey = "methodHooks"
    abstract var methodHooks: Array<MethodHookHolder>
}