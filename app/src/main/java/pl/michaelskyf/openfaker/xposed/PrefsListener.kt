package pl.michaelskyf.openfaker.xposed

class PrefsListener {

    public final val prefName = "xposed_prefs_conf"

    fun reload(): Boolean {
        // This function doesn't do anything, since it's used only for signalling
        // when the module has to reload the shared preferences

        return false
    }
}