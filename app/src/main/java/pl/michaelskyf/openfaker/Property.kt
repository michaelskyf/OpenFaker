package pl.michaelskyf.openfaker

import android.graphics.drawable.Drawable

data class Property(val icon: Drawable, val name: String, val getRealValue: () -> String, val fakeValue: String, val isActive: Boolean)