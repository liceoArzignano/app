package it.liceoarzignano.foundation.marks.subject

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView
import it.liceoarzignano.foundation.R
import it.liceoarzignano.foundation.db.entities.Mark
import it.liceoarzignano.foundation.extensions.applyTheme
import it.liceoarzignano.foundation.extensions.asMarkStr
import it.liceoarzignano.foundation.marks.editor.EditorActivity
import it.liceoarzignano.foundation.util.OnContextMenuClickedListener

class SubjectActivity : AppCompatActivity(), OnContextMenuClickedListener<Mark> {
    private lateinit var mCoordinator: CoordinatorLayout
    private lateinit var mGraph: SparkView
    private lateinit var mPreviewView: TextView

    private lateinit var mAdapter: SubjectAdapter
    private lateinit var mViewModel: SubjectViewModel

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        mViewModel = ViewModelProviders.of(this)[SubjectViewModel::class.java]
        val subject = intent.getStringExtra(EXTRA_SUBJECT) ?: throw IllegalStateException(
                "You must specify a subject by providing intent extra \'$EXTRA_SUBJECT\' a value")
        mViewModel.init(subject)

        setContentView(R.layout.activity_subject)

        val toolbar = findViewById<Toolbar>(R.id.subject_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = subject
        toolbar.setNavigationIcon(R.drawable.ic_toolbar_back)
        toolbar.setNavigationOnClickListener { finish() }

        mCoordinator = findViewById(R.id.subject_coordinator)
        mGraph = findViewById(R.id.subject_graph)
        mPreviewView = findViewById(R.id.subject_preview)
        val recyclerView = findViewById<RecyclerView>(R.id.subject_list)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        mAdapter = SubjectAdapter(this, this)
        recyclerView.adapter = mAdapter

        mViewModel.list.observe(this, Observer(this::update))
    }

    override fun onEdit(item: Mark) {
        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra(EditorActivity.EXTRA_MARK, item)
        startActivity(intent)
    }

    override fun onShare(item: Mark) {
        val message = getString(R.string.marks_share_message, item.value.asMarkStr(), item.subject)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(intent, getString(R.string.action_share)))
    }

    override fun onDelete(item: Mark) {
        MaterialDialog.Builder(this)
                .title(R.string.marks_delete_title)
                .content(R.string.marks_delete_message)
                .positiveText(R.string.action_delete)
                .onPositive { _, _ ->
                    mViewModel.delete(item)
                    Snackbar.make(mCoordinator, R.string.action_deleted, Snackbar.LENGTH_LONG)
                            .applyTheme(this)
                            .show()
                }
                .negativeText(android.R.string.cancel)
                .show()
    }

    private fun update(list: List<Mark>?) {
        if (list == null || list.isEmpty()) {
            finish()
        } else {
            mAdapter.list = list
            mGraph.adapter = GraphAdapter()

            val values = mViewModel.getPreviewValues()
            if (values.isNotEmpty()) {
                mPreviewView.text = getString(R.string.marks_preview, values[0],
                        values[1].asMarkStr())
            }
        }
    }

    inner class GraphAdapter : SparkAdapter() {
        private val mData = mViewModel.fetchGraphData()

        override fun getCount() = mData.size
        override fun getItem(index: Int) = mData[index]
        override fun getY(index: Int) = mData[index]
    }

    companion object {
        const val EXTRA_SUBJECT = "extra_subject"
    }
}
