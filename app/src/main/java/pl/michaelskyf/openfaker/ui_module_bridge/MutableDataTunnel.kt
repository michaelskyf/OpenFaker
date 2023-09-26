package pl.michaelskyf.openfaker.ui_module_bridge

interface MutableDataTunnel: DataTunnel {
    operator fun set(className: String, methodName: String, hookData: Array<HookData>): Result<Unit>
    fun edit(action: Editor.() -> Unit)
    fun edit(): Editor
    interface Editor {
        fun putMethodData(methodData: MethodData): Result<Editor>
        fun remove(methodData: MethodData): Result<Editor>
        fun commit(): Boolean
    }
}