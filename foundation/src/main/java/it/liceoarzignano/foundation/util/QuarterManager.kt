package it.liceoarzignano.foundation.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Environment
import android.preference.PreferenceManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import it.liceoarzignano.foundation.db.entities.Mark
import it.liceoarzignano.foundation.extensions.*
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.collections.HashMap

class QuarterManager(private val activity: Activity) {
    val onExport: (List<Mark>, () -> Unit) -> Unit = this::onExportImpl

    var onShowPrompt: (Boolean) -> Unit = {}

    fun show() {
        onShowPrompt(hasStoragePermission())

        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        prefs.edit { editor ->
            editor[PreferenceKeys.KEY_LAST_QUARTER_DATE] = Date().format(STORE_DATE_FORMAT)
        }
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_ACCESS)
    }

    private fun onExportImpl(data: List<Mark>, onPost: () -> Unit) {
        val today = Calendar.getInstance()
        val fileName = "Liceo ${today[Calendar.YEAR]}-${today[Calendar.MONTH] + 1}.txt"

        val file = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), fileName)

        ExportTask(file, onPost).execute(data)
    }

    private fun hasStoragePermission() = ContextCompat.checkSelfPermission(activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private class ExportTask(private val file: File, private val onPost: () -> Unit) :
            AsyncTask<List<Mark>, Unit, Boolean>() {

        override fun doInBackground(vararg lists: List<Mark>?): Boolean {
            val list = lists[0] ?: return false
            val marksMap = buildMap(list)

            val writer = FileWriter(file)
            val builder = StringBuilder()
            builder.append("# ${file.nameWithoutExtension}\n")

            for (key in marksMap.keys) {
                builder.append("\n## $key\n\n")

                for (mark in marksMap.getOrDefault(key, mutableListOf())) {
                    builder.append("* ${mark.value} - ${mark.date.format("yyyy-MM-dd")}\n")
                }
            }

            file.mkdirs()
            writer.write(builder.toString())
            writer.close()

            // Take a nap so the user can see the progress dialog
            Thread.sleep(1000)
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            onPost()
        }

        private fun buildMap(list: List<Mark>): Map<String, MutableList<Mark>> {
            val map = HashMap<String, MutableList<Mark>>()

            for (item in list) {
                if (map[item.subject] == null) {
                    map[item.subject] = mutableListOf()
                }
                map[item.subject]?.add(item)
            }

            return map
        }
    }

    companion object {
        const val REQUEST_STORAGE_ACCESS = 591
        private const val DEFAULT_DATE = "1971-01"
        private const val STORE_DATE_FORMAT = "yyyy-MM"

        fun shouldDo(context: Context): Boolean {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)

            val thisMonth = Date().getMonthX()
            val lastDate = Date()
            lastDate.parse(prefs[PreferenceKeys.KEY_LAST_QUARTER_DATE,
                    DEFAULT_DATE], STORE_DATE_FORMAT)

            return (thisMonth == 2 || thisMonth == 9) && Date().diff(lastDate) !in 0..28
        }
    }
}
