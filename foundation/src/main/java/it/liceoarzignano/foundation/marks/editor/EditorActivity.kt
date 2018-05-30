package it.liceoarzignano.foundation.marks.editor

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.liceoarzignano.foundation.R
import it.liceoarzignano.foundation.db.entities.Mark
import it.liceoarzignano.foundation.extensions.*
import it.liceoarzignano.foundation.util.PreferenceKeys
import it.liceoarzignano.foundation.util.VibrationHelper
import java.util.*

class EditorActivity : AppCompatActivity() {
    private lateinit var mCoordinator: CoordinatorLayout
    private lateinit var mSubjectLayout: TextInputLayout
    private lateinit var mSubjectView: EditText
    private lateinit var mValuePreview: TextView
    private lateinit var mValueSeekBar: AppCompatSeekBar
    private lateinit var mDateInputLayout: TextInputLayout
    private lateinit var mDateView: EditText
    private lateinit var mNotesView: EditText
    private lateinit var mSaveButton: AppCompatButton

    private lateinit var mViewModel: EditorViewModel
    private lateinit var mSubjects: Array<String>

    private lateinit var mMark: Mark

    private lateinit var mOnDateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProviders.of(this)[EditorViewModel::class.java]

        setContentView(R.layout.activity_marks_editor)

        val toolbar = findViewById<Toolbar>(R.id.marks_editor_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back)
        toolbar.setNavigationOnClickListener { finish() }

        mCoordinator = findViewById(R.id.marks_editor_coordinator)
        mSubjectLayout = findViewById(R.id.marks_editor_subject_layout)
        mSubjectView = findViewById(R.id.marks_editor_subject)
        mValuePreview = findViewById(R.id.marks_editor_value_preview)
        mValueSeekBar = findViewById(R.id.marks_editor_value)
        mDateInputLayout = findViewById(R.id.marks_editor_date_layout)
        mDateView = findViewById(R.id.marks_editor_date)
        mNotesView = findViewById(R.id.marks_editor_notes)
        mSaveButton = findViewById(R.id.marks_editor_save)

        mSubjectView.setOnClickListener { onSubjectClicked() }
        mValueSeekBar.setOnSeekBarChangeListener(ValueSeekBarChangeListener())
        mDateView.setOnClickListener { onDateClicked() }
        mSaveButton.setOnClickListener { onSaveClicked() }

        mMark = intent.getParcelableExtra(EXTRA_MARK) ?: Mark()

        mSubjects = getSubjects()
        mDateView.addTextChangedListener(DateWatcher())
        mOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            onDateSelected(year, month, day)
        }

        insertMarkValuesIfNeeded()
    }

    private fun getSubjects(): Array<String> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs[PreferenceKeys.KEY_ADDRESS, "0"].toInt().getSubjects(this)
    }

    private fun onSubjectClicked() {
        MaterialDialog.Builder(this)
                .title(R.string.marks_editor_hint_subject)
                .items(mSubjects.asList())
                .itemsCallback({ _, _, _, selected ->
                    mSubjectView.text = SpannableStringBuilder(selected)
                    mSubjectLayout.isErrorEnabled = false
                    mMark.subject = selected.toString()
                })
                .show()
    }

    private fun onValueChanged(value: Float) {
        mMark.value = value
        mValuePreview.text = value.asMarkStr()
    }

    private fun onDateClicked() {
        val date = mMark.date.getYMD()
        DatePickerDialog(this, mOnDateSetListener, date[0], date[1], date[2]).show()
    }

    private fun onDateSelected(year: Int, month: Int, day: Int) {
        mMark.date.parse(year, month, day)
        updateDatePreview()
    }

    private fun updateDatePreview() {
        val preview = mMark.date.format(getString(R.string.editor_date_format))
        mDateView.text = SpannableStringBuilder(preview)
    }

    private fun onSaveClicked() {
        if (!checkStatus()) {
            VibrationHelper.vibrateForError(this)
            return
        }

        mMark.notes = mNotesView.text.toString()

        mViewModel.saveMark(mMark)
        Snackbar.make(mCoordinator, R.string.action_saved, Snackbar.LENGTH_LONG)
                .applyTheme(this)
                .show()
        Handler().postDelayed(this::finish, 500)

    }

    private fun checkStatus(): Boolean {
        var result = true

        if (mSubjectView.text.isNullOrBlank()) {
            mSubjectLayout.error = "!"
            mSubjectLayout.isErrorEnabled = true
            result = false
        }

        if (mDateInputLayout.isErrorEnabled) {
            result = false
        }

        if (mMark.value == 0f) {
            Snackbar.make(mCoordinator, R.string.marks_editor_error_value, Snackbar.LENGTH_LONG)
                    .applyTheme(this)
                    .show()
            result = false
        }

        return result
    }

    private fun insertMarkValuesIfNeeded() {
        val isEdit = mMark.value != 0f

        if (isEdit) {
            mSubjectView.text = SpannableStringBuilder(mMark.subject)
            mNotesView.text = SpannableStringBuilder(mMark.notes)
        }

        mValueSeekBar.progress = mMark.value.toInt() * 4
        updateDatePreview()

        supportActionBar?.setTitle(
                if (isEdit) R.string.marks_editor_title_update
                else R.string.marks_editor_title_new)
    }

    inner class ValueSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onValueChanged(progress / 4f)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
    }

    private inner class DateWatcher : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            if (mMark.date.after(Date())) {
                mDateInputLayout.error = "!"
                mDateInputLayout.isErrorEnabled = true
            } else {
                mDateInputLayout.isErrorEnabled = false
            }
        }
    }

    companion object {
        const val EXTRA_MARK = "extra_parcel_mark"
    }
}
