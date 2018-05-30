package it.liceoarzignano.foundation.extensions

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.StyleRes

@ColorInt
fun Context.getColorAttr(@StyleRes style: Int, @AttrRes attr: Int): Int {
    val array = obtainStyledAttributes(style, intArrayOf(attr))
    return array.getColor(0, Color.BLACK).also { array.recycle() }
}

fun Context.getDimen(@DimenRes dimenId: Int) = resources.getDimensionPixelSize(dimenId)