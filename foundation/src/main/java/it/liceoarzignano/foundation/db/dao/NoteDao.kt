package it.liceoarzignano.foundation.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.liceoarzignano.foundation.db.converters.DateConverter
import it.liceoarzignano.foundation.db.entities.Note

@Dao
@TypeConverters(DateConverter::class)
interface NoteDao {

    @get:Query("SELECT * FROM notes ORDER BY date DESC")
    val all: LiveData<List<Note>>

    @get:Query("SELECT * FROM notes ORDER BY date DESC")
    val allStatic: List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg note: Note)

    @Query("SELECT * FROM notes WHERE uid = :arg0")
    fun getById(arg0: Long): List<Note>

    @Delete
    fun delete(note: Note)
}