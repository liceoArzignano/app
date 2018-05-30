package it.liceoarzignano.foundation.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.liceoarzignano.foundation.db.converters.DateConverter
import it.liceoarzignano.foundation.db.entities.Mark

@Dao
@TypeConverters(DateConverter::class)
interface MarkDao {

    @get:Query("SELECT * FROM marks ORDER BY date DESC")
    val all: LiveData<List<Mark>>

    @get:Query("SELECT * FROM marks ORDER BY date DESC")
    val allStatic: List<Mark>

    @Query("SELECT * FROM marks WHERE subject = :arg0 ORDER BY date DESC")
    fun getBySubject(arg0: String): LiveData<List<Mark>>

    @Query("SELECT * FROM marks WHERE subject = :arg0 ORDER BY date DESC")
    fun getBySubjectStatic(arg0: String): List<Mark>


    @Query("SELECT * FROM marks WHERE uid = :arg0")
    fun getById(arg0: Long): List<Mark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg marks: Mark)

    @Delete
    fun delete(mark: Mark)
}