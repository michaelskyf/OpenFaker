package pl.michaelskyf.openfaker.ui

import android.graphics.drawable.Drawable

data class Property(val icon: Drawable, val name: String, val getRealValue: () -> String, val isActive: Boolean)