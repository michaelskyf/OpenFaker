package pl.michaelskyf.openfaker.xposed

class XposedPrefsListener {

    public final val prefName = "xposed_prefs_conf"

    fun reload() {
        // This function doesn't do anything, since it's used only for signalling
        // when the module has to reload the shared preferences
    }
}