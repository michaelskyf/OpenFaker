package pl.michaelskyf.openfaker.xposed

data class LoadPackageParam (val packageName: String, val classLoader: ClassLoader)