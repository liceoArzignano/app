/*
 * Copyright (c) 2018 Liceo L. Da Vinci, Arzignano (VI)
 * Copyright (c) 2018 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.
 */
package it.liceoarzignano.bold.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.liceoarzignano.bold.data.converters.CalendarConverter
import it.liceoarzignano.bold.data.converters.ReminderCategoryConverter
import it.liceoarzignano.bold.data.dao.MarkDao
import it.liceoarzignano.bold.data.dao.PingDao
import it.liceoarzignano.bold.data.dao.ReminderDao
import it.liceoarzignano.bold.data.entities.Mark
import it.liceoarzignano.bold.data.entities.Ping
import it.liceoarzignano.bold.data.entities.Reminder
import it.liceoarzignano.bold.utilities.SingletonHolder

@Database(entities = [(Mark::class), (Ping::class), (Reminder::class)], version = 1)
@TypeConverters(CalendarConverter::class, ReminderCategoryConverter::class)
abstract class AppDatabase protected constructor(): RoomDatabase() {

    abstract fun markDao(): MarkDao
    abstract fun pingDao(): PingDao
    abstract fun reminderDao(): ReminderDao

    companion object : SingletonHolder<AppDatabase, Context>({
        if (AppDatabase.TEST_MODE)
            Room.inMemoryDatabaseBuilder(it.applicationContext, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        else
            Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "bold_db")
                .build()

    }) {
        // This is used during unit tests
        var TEST_MODE = false

        /* Migrations */
    }
}