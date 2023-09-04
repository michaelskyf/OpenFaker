package pl.michaelskyf.openfaker.ui_module_bridge

data class HookerData(val className: String, val methodName: String, val argumentTypes: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HookerData

        if (className != other.className) return false
        if (methodName != other.methodName) return false
        if (!argumentTypes.contentEquals(other.argumentTypes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = className.hashCode()
        result = 31 * result + methodName.hashCode()
        result = 31 * result + argumentTypes.contentHashCode()
        return result
    }
}