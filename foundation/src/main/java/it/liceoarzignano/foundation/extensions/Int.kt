package it.liceoarzignano.foundation.extensions

import android.content.Context
import it.liceoarzignano.foundation.R

fun Int.getSubjects(context: Context): Array<String> {
    val resource = when (this) {
        0 -> R.array.subjects_address_0
        1 -> R.array.subjects_address_1
        2 -> R.array.subjects_address_2
        3 -> R.array.subjects_address_3
        4 -> R.array.subjects_address_4
        else -> return emptyArray()
    }

    return context.resources.getStringArray(resource)
}

fun Int.getAddressName(context: Context): String = context.getString(when (this) {
    1 -> R.string.address_1
    2 -> R.string.address_2
    3 -> R.string.address_3
    4 -> R.string.address_4
    else -> R.string.address_0
})
