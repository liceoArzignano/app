package it.liceoarzignano.foundation.marks.subject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.liceoarzignano.foundation.db.AppDatabase
import it.liceoarzignano.foundation.db.DatabaseTask
import it.liceoarzignano.foundation.db.entities.Mark

class SubjectViewModel(owner: Application) : AndroidViewModel(owner) {
    private val mDatabase = AppDatabase.getInstance(owner)
    lateinit var list: LiveData<List<Mark>>
    private lateinit var mSubject: String

    fun init(subject: String) {
        list = mDatabase.marks().getBySubject(subject)
        mSubject = subject
    }

    fun delete(mark: Mark) {
        DeleteTask(mDatabase).execute(mark)
    }

    fun fetchGraphData(): Array<Float> {
        val task = FetchGraphDataTask(mDatabase)
        task.execute(mSubject)
        return task.get()
    }

    fun getPreviewValues(): Array<Float> {
        val task = GetPreviewValues(mDatabase)
        task.execute(mSubject)
        return task.get()
    }

    private class DeleteTask(db: AppDatabase) : DatabaseTask<Mark, Unit>(db) {
        override fun doInBackground(vararg params: Mark?) {
            val mark = params[0] ?: return
            db.marks().delete(mark)
        }
    }

    private class FetchGraphDataTask(db: AppDatabase) : DatabaseTask<String, Array<Float>>(db) {
        override fun doInBackground(vararg subjects: String?): Array<Float> {
            val subject = subjects[0] ?: return emptyArray()
            val list = db.marks().getBySubjectStatic(subject)

            // Graph needs 2 points to draw a line
            if (list.size == 1) {
                return arrayOf(list[0].value, list[0].value)
            }

            val result = Array(list.size, { 0f })
            for ((i, item) in list.withIndex()) {
                result[i] = item.value
            }

            return result
        }
    }

    private class GetPreviewValues(db: AppDatabase) : DatabaseTask<String, Array<Float>>(db) {
        override fun doInBackground(vararg params: String?): Array<Float> {
            val subject = params[0] ?: return arrayOf()
            val list = db.marks().getBySubjectStatic(subject)

            val sum = list.sumByDouble { it.value.toDouble() }
            val average = sum / list.size
            val expected = 6 * (list.size + 1) - sum

            return arrayOf(average.toFloat(), expected.toFloat())
        }
    }
}