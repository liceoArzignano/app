package it.liceoarzignano.foundation.extensions

import android.os.Parcel
import java.util.*

fun Parcel.readBoolean() = readInt() == 1

fun Parcel.writeBoolean(value: Boolean) {
    writeInt(if (value) 1 else 0)
}

fun Parcel.readDate() = Date(readLong())

fun Parcel.writeDate(value: Date) {
    writeLong(value.time)
}
