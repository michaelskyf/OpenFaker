package pl.michaelskyf.openfaker.ui_module_bridge

import pl.michaelskyf.openfaker.module.FakerModule

data class MethodHookHolder(
    val className: String,
    val methodName: String,
    val argumentTypes: Array<String>,
    val fakerModule: FakerModule,
    val whenToHook: WhenToHook
) {
    enum class WhenToHook {
        Before,
        After
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MethodHookHolder

        if (className != other.className) return false
        if (methodName != other.methodName) return false
        if (!argumentTypes.contentEquals(other.argumentTypes)) return false
        if (fakerModule != other.fakerModule) return false
        if (whenToHook != other.whenToHook) return false

        return true
    }

    override fun hashCode(): Int {
        var result = className.hashCode()
        result = 31 * result + methodName.hashCode()
        result = 31 * result + argumentTypes.contentHashCode()
        result = 31 * result + fakerModule.hashCode()
        result = 31 * result + whenToHook.hashCode()
        return result
    }
}