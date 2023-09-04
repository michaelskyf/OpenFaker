package pl.michaelskyf.openfaker.ui_module_bridge

import kotlinx.serialization.Serializable

@Serializable
class HookHandlerData(
    val whichPackages: WhichPackages,
    val fakerModuleFactory: FakerModuleFactory,
    val whenToHook: WhenToHook
) {
    @Serializable
    sealed class WhichPackages {
        data object All : WhichPackages()
        data class Some(val packages: HashSet<String>) : WhichPackages()

        fun isMatching(packageName: String): Boolean
            = when(this) {
                is All -> true
                is Some -> this.packages.contains(packageName)
        }
    }

    enum class WhenToHook {
        Before,
        After
    }
}