package pl.michaelskyf.openfaker.xposed

import de.robv.android.xposed.XposedBridge
import pl.michaelskyf.openfaker.module.Logger

class XLogger : Logger {
    override fun log(message: String) {
        XposedBridge.log("OpenFaker: $message")
    }
}