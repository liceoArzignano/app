package it.liceoarzignano.thin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.liceoarzignano.foundation.db.AppDatabase
import it.liceoarzignano.foundation.db.DatabaseTask
import it.liceoarzignano.foundation.db.entities.Mark
import it.liceoarzignano.foundation.db.entities.Ping

class MainViewModel(owner: Application) : AndroidViewModel(owner) {
    val marksList: LiveData<List<Mark>>
    val pingList: LiveData<List<Ping>>

    private val mDatabase = AppDatabase.getInstance(owner)

    init {
        marksList = mDatabase.marks().all
        pingList = mDatabase.pings().all
    }

    fun getOverviewMarks(subjects: Array<String>): List<Mark> {
        val task = FetchMarkOverviewTask(mDatabase)
        task.execute(subjects)
        return task.get()
    }

    fun getMarksStatic(): List<Mark> {
        val task = GetAllMarksTask(mDatabase)
        task.execute()
        return task.get()
    }

    fun deleteAllMarks(onPost: () -> Unit) {
        DeleteAllMarksTask(mDatabase, onPost).execute()
    }

    private class FetchMarkOverviewTask(db: AppDatabase) :
            DatabaseTask<Array<String>, List<Mark>>(db) {
        override fun doInBackground(vararg params: Array<String>?): List<Mark> {
            val subjects = params[0] ?: return emptyList()
            val result = mutableListOf<Mark>()

            for (subject in subjects) {
                var average = 0f
                val marksOfSubject = db.marks().getBySubjectStatic(subject)
                if (marksOfSubject.isEmpty()) {
                    continue
                }

                for (mark in marksOfSubject) {
                    average += mark.value
                }

                val overview = Mark()
                overview.subject = subject
                overview.value = average / marksOfSubject.size
                result.add(overview)
            }

            return result
        }
    }

    private class GetAllMarksTask(db: AppDatabase) : DatabaseTask<Unit, List<Mark>>(db) {
        override fun doInBackground(vararg params: Unit?): List<Mark> {
            return db.marks().allStatic
        }
    }

    private class DeleteAllMarksTask(db: AppDatabase, private val onPost: () -> Unit) :
            DatabaseTask<Unit, Unit>(db) {
        override fun doInBackground(vararg params: Unit?) {
            db.marks().allStatic.forEach { item -> db.marks().delete(item) }

            // Take a nap so the user can see the progress dialog
            Thread.sleep(1000)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            onPost()
        }
    }
}