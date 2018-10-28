/*
 * Copyright (c) 2018 Liceo L. Da Vinci, Arzignano (VI)
 * Copyright (c) 2018 Bevilacqua Joey
 *
 * Licensed under the GNU GPLv3 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl.
 */
package it.liceoarzignano.bold.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.liceoarzignano.bold.data.entities.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY time DESC")
    fun getLive(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminders ORDER BY time DESC")
    fun getPaged(): DataSource.Factory<Int, Reminder>

    @Query("SELECT * FROM reminders WHERE uid = :uid")
    fun getById(uid: Long): Reminder

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg reminder: Reminder)

    @Delete
    fun delete(reminder: Reminder)
}