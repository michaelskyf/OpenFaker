package pl.michaelskyf.openfaker.ui_module_bridge

class HookData(
    val argumentTypes: Array<String>,
    val fakerModuleFactory: FakerModuleFactory,
    val whenToHook: WhenToHook
) {
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