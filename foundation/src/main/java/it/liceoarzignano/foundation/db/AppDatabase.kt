package it.liceoarzignano.foundation.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.liceoarzignano.foundation.db.converters.DateConverter
import it.liceoarzignano.foundation.db.dao.MarkDao
import it.liceoarzignano.foundation.db.dao.NoteDao
import it.liceoarzignano.foundation.db.dao.PingDao
import it.liceoarzignano.foundation.db.entities.Mark
import it.liceoarzignano.foundation.db.entities.Note
import it.liceoarzignano.foundation.db.entities.Ping
import it.liceoarzignano.foundation.util.SingletonHolder

@Database(entities = [(Mark::class), (Note::class), (Ping::class)], version = 1)
@TypeConverters(DateConverter::class)
abstract class AppDatabase protected constructor() : RoomDatabase() {

    abstract fun marks(): MarkDao
    abstract fun notes(): NoteDao
    abstract fun pings(): PingDao

    companion object : SingletonHolder<AppDatabase, Context>({
        if (AppDatabase.TEST_MODE) {
            Room.inMemoryDatabaseBuilder(it, AppDatabase::class.java)
                    .allowMainThreadQueries()
                    .build()
        } else {
            // Add migrations when needed
            Room.databaseBuilder(it, AppDatabase::class.java, "foundation_database")
                    .build()
        }
    }){
        // This is used during unit tests
        var TEST_MODE = false
    }
}