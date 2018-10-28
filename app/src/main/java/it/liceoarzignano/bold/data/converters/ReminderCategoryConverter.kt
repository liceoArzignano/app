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
import it.liceoarzignano.bold.data.entities.ReminderCategory

class ReminderCategoryConverter {

    @TypeConverter
    fun toInt(value: ReminderCategory?): Int? = value?.ordinal ?: 0

    @TypeConverter
    fun toReminderCategory(value: Int?): ReminderCategory? = when (value) {
        1 -> ReminderCategory.TODO
        2 -> ReminderCategory.TEST
        3 -> ReminderCategory.ASSIGNMENT
        4 -> ReminderCategory.BIRTHDAY
        else -> ReminderCategory.REMINDER
    }
}