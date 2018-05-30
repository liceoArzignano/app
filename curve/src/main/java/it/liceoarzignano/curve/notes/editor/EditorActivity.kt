package it.liceoarzignano.curve.notes.editor

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.liceoarzignano.curve.R
import it.liceoarzignano.foundation.db.entities.Note
import it.liceoarzignano.foundation.extensions.applyTheme
import it.liceoarzignano.foundation.extensions.format
import it.liceoarzignano.foundation.extensions.getYMD
import it.liceoarzignano.foundation.extensions.parse
import it.liceoarzignano.foundation.util.VibrationHelper

class EditorActivity : AppCompatActivity() {
    private lateinit var mCoordinator: CoordinatorLayout
    private lateinit var mTitleLayout: TextInputLayout
    private lateinit var mTitleView: EditText
    private lateinit var mCategoryView: EditText
    private lateinit var mDateView: EditText
    private lateinit var mContentView: EditText
    private lateinit var mSaveButton: AppCompatButton

    private lateinit var mViewModel: EditorViewModel
    private lateinit var mCategories: Array<String>

    private lateinit var mNote: Note

    private lateinit var mOnDateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel = ViewModelProviders.of(this)[EditorViewModel::class.java]

        setContentView(R.layout.activity_notes_editor)

        val toolbar = findViewById<Toolbar>(R.id.notes_editor_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back)
        toolbar.setNavigationOnClickListener { finish() }

        mCoordinator = findViewById(R.id.notes_editor_coordinator)
        mTitleLayout = findViewById(R.id.notes_editor_title_layout)
        mTitleView = findViewById(R.id.notes_editor_title)
        mCategoryView = findViewById(R.id.notes_editor_category)
        mDateView = findViewById(R.id.note_editor_date)
        mContentView = findViewById(R.id.notes_editor_content)
        mSaveButton = findViewById(R.id.notes_editor_save)

        mCategoryView.setOnClickListener { onCategoryClicked() }
        mDateView.setOnClickListener { onDateClicked() }
        mSaveButton.setOnClickListener { onSaveClicked() }

        mNote = intent.getParcelableExtra(EXTRA_NOTE) ?: Note()

        mTitleView.addTextChangedListener(TitleWatcher())
        mCategories = resources.getStringArray(R.array.note_categories)
        mOnDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            onDateSelected(year, month, day)
        }

        insertNoteValuesIfNeeded()
    }

    private fun onCategoryClicked() {
        MaterialDialog.Builder(this)
                .title(R.string.notes_editor_hint_category)
                .items(mCategories.asList())
                .itemsCallback({ _, _, which, _ ->
                    mCategoryView.text = SpannableStringBuilder(mCategories[which])
                    mNote.tag = which.toLong()
                })
                .show()
    }

    private fun onDateClicked() {
        val date = mNote.date.getYMD()
        DatePickerDialog(this, mOnDateSetListener, date[0], date[1], date[2]).show()
    }

    private fun onDateSelected(year: Int, month: Int, day: Int) {
        mNote.date.parse(year, month, day)
        updateDatePreview()
    }

    private fun updateDatePreview() {
        val preview = mNote.date.format(getString(R.string.editor_date_format))
        mDateView.text = SpannableStringBuilder(preview)
    }

    private fun onSaveClicked() {
        if (!checkStatus()) {
            VibrationHelper.vibrateForError(this)
            return
        }

        mNote.title = mTitleView.text.toString()
        mNote.content = mContentView.text.toString()

        mViewModel.saveNote(mNote)
        Snackbar.make(mCoordinator, R.string.action_saved, Snackbar.LENGTH_LONG)
                .applyTheme(this)
                .show()
        Handler().postDelayed(this::finish, 500)
    }

    private fun checkStatus(): Boolean {
        var result = true

        if (mTitleView.text.isNullOrBlank()) {
            mTitleLayout.error = "!"
            mTitleLayout.isErrorEnabled = true
            result = false
        }

        return result
    }

    private fun insertNoteValuesIfNeeded() {
        val isEdit = mNote.title != ""

        if (isEdit) {
            mTitleView.text = SpannableStringBuilder(mNote.title)
            mContentView.text = SpannableStringBuilder(mNote.content)
            mCategoryView.text = SpannableStringBuilder(mCategories[mNote.tag.toInt()])
        }

        updateDatePreview()

        supportActionBar?.setTitle(
                if (isEdit) R.string.notes_editor_title_update
                else R.string.notes_editor_title_new)
    }

    private inner class TitleWatcher : TextWatcher {
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun afterTextChanged(p0: Editable?) {
            if (!p0?.toString().isNullOrBlank()) {
                mTitleLayout.isErrorEnabled = false
            } else {
                mTitleLayout.error = "!"
                mTitleLayout.isErrorEnabled = true
            }
        }
    }

    companion object {
        const val EXTRA_NOTE = "extra_parcel_note"

    }
}