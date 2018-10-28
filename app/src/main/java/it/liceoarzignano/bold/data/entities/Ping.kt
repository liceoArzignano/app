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

@Entity(tableName = "pings")
data class Ping(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val title: String,
    val content: String,
    val url: String,
    @TypeConverters(CalendarConverter::class)
    val time: Calendar
) {

    override fun equals(other: Any?) = other != null &&
        other is Ping &&
        other.title == title &&
        other.content == content &&
        other.url == url &&
        other.time == time

    override fun hashCode() = super.hashCode() + 1
}