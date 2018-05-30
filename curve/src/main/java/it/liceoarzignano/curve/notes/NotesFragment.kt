package it.liceoarzignano.curve.notes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import it.liceoarzignano.curve.MainActivity
import it.liceoarzignano.curve.R
import it.liceoarzignano.curve.notes.editor.EditorActivity
import it.liceoarzignano.foundation.db.entities.Note
import it.liceoarzignano.foundation.extensions.format
import it.liceoarzignano.foundation.util.OnContextMenuClickedListener
import it.liceoarzignano.foundation.widgets.MainFragment

class NotesFragment : MainFragment<Note, NotesAdapter>(), OnContextMenuClickedListener<Note> {

    override val adapterLayoutManager: RecyclerView.LayoutManager
        get() = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notes, container, false)

        recyclerView = view.findViewById(R.id.fragment_notes_list)
        emptyView = view.findViewById(R.id.fragment_notes_empty)

        if (context != null) {
            adapter = NotesAdapter(context!!, this)
            setupRecyclerView()
        }

        return view
    }

    override fun onDelete(item: Note) {
        val act = (activity ?: return) as? MainActivity ?: return

        MaterialDialog.Builder(act)
                .title(R.string.notes_delete_title)
                .content(R.string.notes_delete_message)
                .positiveText(R.string.action_delete)
                .onPositive { _, _ ->
                    act.deleteNote(item)
                }
                .negativeText(android.R.string.cancel)
                .show()
    }

    override fun onEdit(item: Note) {
        val intent = Intent(activity, EditorActivity::class.java)
        intent.putExtra(EditorActivity.EXTRA_NOTE, item)
        startActivity(intent)
    }

    override fun onShare(item: Note) {
        val message = getString(R.string.notes_share_message, item.title, item.content,
                item.date.format("yyyy-MM-dd"))

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(intent, getString(R.string.action_share)))

    }

    override fun onItemClick(item: Note) = Unit

    override fun shouldShowFab() = true

    override fun onFabClicked() {
       val intent = Intent(activity, EditorActivity::class.java)
        startActivity(intent)
    }
}