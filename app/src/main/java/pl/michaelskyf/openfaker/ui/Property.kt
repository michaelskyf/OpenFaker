package pl.michaelskyf.openfaker.ui

import android.graphics.drawable.Drawable
import pl.michaelskyf.openfaker.module.JsonToMap

data class Property(val icon: Drawable, val name: String, val getRealValue: () -> String, val data: JsonToMap.MethodArguments, val isActive: Boolean)