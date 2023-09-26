package pl.michaelskyf.openfaker.ui_module_bridge

import kotlinx.serialization.Serializable

@Serializable
class HookData(
    val whichPackages: WhichPackages,
    val argumentTypes: Array<String>,
    val fakerModuleFactory: FakerModuleFactory,
    val whenToHook: WhenToHook
) {
    @Serializable
    sealed class WhichPackages {
        @Serializable
        data object All : WhichPackages()
        @Serializable
        data class Some(val packages: HashSet<String>) : WhichPackages()

        fun isMatching(packageName: String): Boolean
                = when(this) {
            is All -> true
            is Some -> packages.contains(packageName)
        }
    }

    enum class WhenToHook {
        Before,
        After
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HookData

        if (whichPackages != other.whichPackages) return false
        if (!argumentTypes.contentEquals(other.argumentTypes)) return false
        if (fakerModuleFactory != other.fakerModuleFactory) return false
        if (whenToHook != other.whenToHook) return false

        return true
    }

    override fun hashCode(): Int {
        var result = whichPackages.hashCode()
        result = 31 * result + argumentTypes.contentHashCode()
        result = 31 * result + fakerModuleFactory.hashCode()
        result = 31 * result + whenToHook.hashCode()
        return result
    }
}