package it.liceoarzignano.foundation.marks.editor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.liceoarzignano.foundation.db.AppDatabase
import it.liceoarzignano.foundation.db.DatabaseTask
import it.liceoarzignano.foundation.db.entities.Mark

class EditorViewModel(owner: Application) : AndroidViewModel(owner) {
    private val mDatabase = AppDatabase.getInstance(owner)

    fun saveMark(mark: Mark) {
        SaveTask(mDatabase).execute(mark)
    }

    private class SaveTask(db: AppDatabase) : DatabaseTask<Mark, Unit>(db) {

        override fun doInBackground(vararg params: Mark?) {
            val mark = params[0] ?: return
            db.marks().insert(mark)
        }
    }
}