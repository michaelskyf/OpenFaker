package pl.michaelskyf.openfaker.ui_module_bridge

interface DataTunnel {
    operator fun get(className: String, methodName: String): Result<MethodData>
    fun hasHookChanged(className: String, methodName: String): Boolean
    fun getAllHooks(): Result<List<MethodData>>
}