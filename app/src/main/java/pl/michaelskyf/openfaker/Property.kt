package pl.michaelskyf.openfaker

import android.graphics.drawable.Drawable
import pl.michaelskyf.openfaker.xposed.JsonToMap

data class Property(val icon: Drawable, val name: String, val getRealValue: () -> String, val data: JsonToMap.MethodArguments, val isActive: Boolean)