package it.liceoarzignano.foundation.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.liceoarzignano.foundation.db.converters.DateConverter
import it.liceoarzignano.foundation.db.entities.Ping

@Dao
@TypeConverters(DateConverter::class)
interface PingDao {

    @get:Query("SELECT * FROM pings ORDER BY date DESC")
    val all: LiveData<List<Ping>>

    @get:Query("SELECT * FROM pings ORDER BY date DESC")
    val allStatic: List<Ping>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg ping: Ping)

    @Query("SELECT * FROM pings WHERE uid = :arg0")
    fun getById(arg0: Long): List<Ping>
}