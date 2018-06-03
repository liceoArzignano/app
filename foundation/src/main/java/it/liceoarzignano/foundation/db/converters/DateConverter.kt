package it.liceoarzignano.foundation.db.converters

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    @TypeConverter
    fun toDate(value: Long?): Date? =
            if (value == null) Date(0)
            else Date(value)

    @TypeConverter
    fun toLong(value: Date?): Long? = value?.time ?: 0L
}