package pl.michaelskyf.openfaker.ui

import android.graphics.drawable.Drawable
import pl.michaelskyf.openfaker.ui_module_bridge.MethodData

data class Property(val icon: Drawable, val name: String, val getRealValue: () -> String, val methodData: MethodData, var isActive: Boolean)