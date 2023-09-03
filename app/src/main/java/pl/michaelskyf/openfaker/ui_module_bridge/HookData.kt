package pl.michaelskyf.openfaker.ui_module_bridge

import kotlinx.serialization.Serializable

@Serializable
class HookData(
    val whichPackages: WhichPackages,
    val argumentTypes: Array<String>,
    val fakerModuleFactory: FakerModuleFactory,
    val whenToHook: WhenToHook
) {
    // Replaced sealed class with a "normal" class due to slow startup time of
    // kotlin reflections needed to convert the sealed class to json
    @Serializable
    class WhichPackages(
        private val shouldCheck: Boolean,
        private val matchingPackages: Array<String>
    ) {
        object Some{
            operator fun invoke(matchingPackages: Array<String>)
                = WhichPackages(true, matchingPackages)
        }

        object All{
            operator fun invoke()
                = WhichPackages(false, arrayOf())
        }

        fun isMatching(packageName: String): Boolean
            = when(shouldCheck) {
                false -> true
                true -> this.matchingPackages.contains(packageName)
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

        if (!argumentTypes.contentEquals(other.argumentTypes)) return false
        if (fakerModuleFactory != other.fakerModuleFactory) return false
        if (whenToHook != other.whenToHook) return false

        return true
    }

    override fun hashCode(): Int {
        var result = argumentTypes.contentHashCode()
        result = 31 * result + fakerModuleFactory.hashCode()
        result = 31 * result + whenToHook.hashCode()
        return result
    }
}