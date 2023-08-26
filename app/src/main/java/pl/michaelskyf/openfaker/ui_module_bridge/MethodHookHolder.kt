package pl.michaelskyf.openfaker.ui_module_bridge

import pl.michaelskyf.openfaker.module.Priority

data class MethodHookHolder(
    val className: String,
    val methodName: String,
    val argumentTypes: Array<String>,
    val luaScript: String,
    val priority: Priority
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MethodHookHolder

        if (className != other.className) return false
        if (methodName != other.methodName) return false
        if (!argumentTypes.contentEquals(other.argumentTypes)) return false
        if (luaScript != other.luaScript) return false
        if (priority != other.priority) return false

        return true
    }

    override fun hashCode(): Int {
        var result = className.hashCode()
        result = 31 * result + methodName.hashCode()
        result = 31 * result + argumentTypes.contentHashCode()
        result = 31 * result + luaScript.hashCode()
        result = 31 * result + priority.hashCode()
        return result
    }
}