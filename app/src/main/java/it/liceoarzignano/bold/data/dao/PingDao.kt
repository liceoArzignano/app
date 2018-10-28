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

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.liceoarzignano.bold.data.entities.Ping

@Dao
interface PingDao {

    @Query("SELECT * FROM pings ORDER BY time DESC")
    fun getPagedList(): DataSource.Factory<Int, Ping>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ping: Ping)
}