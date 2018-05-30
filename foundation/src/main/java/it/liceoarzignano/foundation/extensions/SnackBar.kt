package it.liceoarzignano.foundation.extensions

import android.content.Context
import android.os.Build
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import it.liceoarzignano.foundation.R

fun Snackbar.applyTheme(context: Context): Snackbar {
    val margin = context.getDimen(R.dimen.snack_bar_margin)
    val elevation = context.getDimen(R.dimen.snack_bar_elevation)

    val params = view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(margin, margin, margin, margin)

    view.background = ContextCompat.getDrawable(context, R.drawable.bg_snackbar)
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        view.elevation = elevation.toFloat()
    }

    return this
}