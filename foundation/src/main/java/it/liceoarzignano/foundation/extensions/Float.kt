package it.liceoarzignano.foundation.extensions

fun Float.asMarkStr(): String {
    val suffix = when (this % 1) {
        0.25f -> "+"
        0.5f -> "½"
        0.75f -> "/${toInt() + 1}"
        else -> ""
    }
    return "${toInt()}$suffix"
}