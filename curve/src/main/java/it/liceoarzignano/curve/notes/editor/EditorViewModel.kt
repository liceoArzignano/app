package it.liceoarzignano.curve.notes.editor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.liceoarzignano.foundation.db.AppDatabase
import it.liceoarzignano.foundation.db.DatabaseTask
import it.liceoarzignano.foundation.db.entities.Note

class EditorViewModel(owner: Application) : AndroidViewModel(owner) {
    private val mDatabase = AppDatabase.getInstance(owner)

    fun saveNote(note: Note) {
        SaveNoteTask(mDatabase).execute(note)
    }

    private class SaveNoteTask(db: AppDatabase) : DatabaseTask<Note, Unit>(db) {

        override fun doInBackground(vararg p0: Note?) {
            val note = p0[0] ?: return
            db.notes().insert(note)
        }
    }
}