package pl.michaelskyf.openfaker.ui_module_bridge

import kotlinx.serialization.Serializable

@Serializable
data class MethodData(val className: String, val methodName: String, val hookData: Array<HookData>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MethodData

        if (className != other.className) return false
        if (methodName != other.methodName) return false
        if (!hookData.contentEquals(other.hookData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = className.hashCode()
        result = 31 * result + methodName.hashCode()
        result = 31 * result + hookData.contentHashCode()
        return result
    }
}