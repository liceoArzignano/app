package it.liceoarzignano.curve.extensions

import android.content.Context
import it.liceoarzignano.curve.R
import it.liceoarzignano.foundation.db.entities.Note

fun Long.asTagString(context: Context): String = context.getString(when (toInt()) {
    Note.GENERIC -> R.string.notes_category_generic
    Note.TEST -> R.string.notes_category_test
    Note.HOMEWORK -> R.string.notes_category_homework
    Note.BIRTHDAY -> R.string.notes_category_birthday
    Note.REMEMBER -> R.string.notes_category_remember
    else -> throw IllegalStateException("$this is not a valid @Tag value")
})