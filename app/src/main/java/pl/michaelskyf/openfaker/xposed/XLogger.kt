package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XposedBridge
import pl.michaelskyf.openfaker.BuildConfig
import pl.michaelskyf.openfaker.module.Logger

class XLogger : Logger() {
    override fun log(tag: String, message: String) {
        XposedBridge.log("$tag: $message")
    }

    override fun log(message: String) {
        XposedBridge.log("OpenFaker: $message")
    }
}