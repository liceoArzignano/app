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
import it.liceoarzignano.bold.data.entities.Mark

@Dao
interface MarkDao {

    @Query("SELECT * FROM marks WHERE subject = :subject ORDER BY time DESC")
    fun getLive(subject: Int): LiveData<List<Mark>>

    @Query("SELECT * FROM marks WHERE subject = :subject ORDER BY time DESC")
    fun getPaged(subject: Int): DataSource.Factory<Int, Mark>

    @Query("SELECT * FROM marks WHERE uid = :uid")
    fun getById(uid: Long): Mark

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg mark: Mark)

    @Delete
    fun delete(mark: Mark)
}