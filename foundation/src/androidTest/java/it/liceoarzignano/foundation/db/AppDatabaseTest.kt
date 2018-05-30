package it.liceoarzignano.foundation.db

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import it.liceoarzignano.foundation.db.dao.MarkDao
import it.liceoarzignano.foundation.db.dao.NoteDao
import it.liceoarzignano.foundation.db.dao.PingDao
import it.liceoarzignano.foundation.db.entities.Mark
import it.liceoarzignano.foundation.db.entities.Note
import it.liceoarzignano.foundation.db.entities.Ping
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private var markDao: MarkDao? = null
    private var noteDao: NoteDao? = null
    private var pingDao: PingDao? = null

    @Before
    fun setup() {
        AppDatabase.TEST_MODE = true
        val instance = AppDatabase.getInstance(InstrumentationRegistry.getContext())
        markDao = instance.marks()
        noteDao = instance.notes()
        pingDao = instance.pings()
    }

    @Test
    fun addMark() {
        val uid: Long = 1
        val item = Mark(uid, 10f, "Test", Date(), "Content test", false)
        markDao?.insert(item)

        val test = markDao?.getById(uid)!![0]
        assert(test == item)
    }

    @Test
    fun removeMark() {
        val uid: Long = 2
        val item = Mark(uid, 6f, "Test", Date(), "Content test", true)

        markDao?.insert(item)
        assert(markDao?.getById(uid)!!.isNotEmpty())

        markDao?.delete(item)
        assert(markDao?.getById(uid)!!.isEmpty())
    }

    @Test
    fun addNote() {
        val uid: Long = 1
        val item = Note(uid, "Test", "Content test", Date(), Note.GENERIC.toLong())
        noteDao?.insert(item)

        val test = noteDao?.getById(uid)!![0]
        assert(test == item)
    }

    @Test
    fun removeNote() {
        val uid: Long = 2
        val item = Note(uid, "Test", "Content test", Date(), Note.TEST.toLong())

        noteDao?.insert(item)
        assert(noteDao?.getById(uid)!!.isNotEmpty())

        noteDao?.delete(item)
        assert(noteDao?.getById(uid)!!.isEmpty())
    }

    @Test
    fun addPing() {
        val uid: Long = 1
        val item = Ping(uid, "Test", "Content test", "", Date())
        pingDao?.insert(item)

        val test = pingDao?.getById(1)!![0]
        assert(test == item)
    }
}