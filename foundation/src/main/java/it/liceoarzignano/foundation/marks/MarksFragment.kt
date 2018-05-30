package it.liceoarzignano.foundation.marks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import it.liceoarzignano.foundation.R
import it.liceoarzignano.foundation.db.entities.Mark
import it.liceoarzignano.foundation.marks.editor.EditorActivity
import it.liceoarzignano.foundation.marks.subject.SubjectActivity
import it.liceoarzignano.foundation.widgets.MainFragment

class MarksFragment : MainFragment<Mark, MarkOverviewAdapter>() {
    override val adapterLayoutManager
            get() = GridLayoutManager(context, 2)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_marks, container, false)

        recyclerView = view.findViewById(R.id.fragment_marks_list)
        emptyView = view.findViewById(R.id.fragment_marks_empty)

        if (context != null) {
            adapter = MarkOverviewAdapter(this::onItemClick)
            setupRecyclerView()
        }

        return view
    }

    override fun onItemClick(item: Mark) {
        val intent = Intent(activity, SubjectActivity::class.java)
        intent.putExtra(SubjectActivity.EXTRA_SUBJECT, item.subject)
        activity?.startActivity(intent)
    }

    override fun shouldShowFab() = true

    override fun onFabClicked() {
        val intent = Intent(activity, EditorActivity::class.java)
        activity?.startActivity(intent)
    }
}
