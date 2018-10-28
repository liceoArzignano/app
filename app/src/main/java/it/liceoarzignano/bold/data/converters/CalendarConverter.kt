/*
 * Copyright (c) 2018 Liceo L. Da Vinci, Arzignano (VI)
 * Copyright (c) 2018 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.
 */
package it.liceoarzignano.bold.data.converters

import androidx.room.TypeConverter
import java.util.Calendar

class CalendarConverter {

    @TypeConverter
    fun toLong(value: Calendar?): Long? = value?.timeInMillis ?: 0L

    @TypeConverter
    fun toDate(value: Long?): Calendar? = Calendar.getInstance().apply { timeInMillis = value ?: 0L }
}