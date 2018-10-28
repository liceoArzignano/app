/*
 * Copyright (c) 2018 Liceo L. Da Vinci, Arzignano (VI)
 * Copyright (c) 2018 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.
 */
package it.liceoarzignano.bold.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.liceoarzignano.bold.data.converters.CalendarConverter
import java.util.Calendar

@Entity(tableName = "marks")
data class Mark(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val subject: Int,
    val grade: Float,
    @TypeConverters(CalendarConverter::class)
    val time: Calendar,
    val comment: String
) {

    override fun equals(other: Any?) = other != null &&
        other is Mark &&
        other.subject == other.subject &&
        other.grade == grade &&
        other.time.timeInMillis == time.timeInMillis

    override fun hashCode() = super.hashCode() + 1
}