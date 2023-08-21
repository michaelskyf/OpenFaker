package pl.michaelskyf.openfaker.ui

import android.graphics.drawable.Drawable
import pl.michaelskyf.openfaker.module.MethodArguments

data class Property(val icon: Drawable, val name: String, val getRealValue: () -> String, val data: MethodArguments, val isActive: Boolean)