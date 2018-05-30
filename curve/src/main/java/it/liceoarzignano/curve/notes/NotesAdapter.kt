package it.liceoarzignano.curve.notes

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import it.liceoarzignano.curve.R
import it.liceoarzignano.curve.extensions.asTagString
import it.liceoarzignano.foundation.db.entities.Note
import it.liceoarzignano.foundation.extensions.diff
import it.liceoarzignano.foundation.extensions.format
import it.liceoarzignano.foundation.util.MainAdapter
import it.liceoarzignano.foundation.util.OnContextMenuClickedListener
import it.liceoarzignano.foundation.widgets.ContextualMenu
import java.util.*

class NotesAdapter(private val mContext: Context,
                   private val mListener: OnContextMenuClickedListener<Note>) :
        MainAdapter<Note, NotesAdapter.NoteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            NoteHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_note, parent, false))

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class NoteHolder(private val mView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(mView) {
        private val mTitleView = mView.findViewById<TextView>(R.id.item_note_title)
        private val mContentView = mView.findViewById<TextView>(R.id.item_note_content)
        private val mTagView = mView.findViewById<TextView>(R.id.item_note_tag)
        private val mDateView = mView.findViewById<TextView>(R.id.item_note_date)

        private lateinit var mNote: Note

        fun bind(note: Note) {
            mNote = note

            mTitleView.text = note.title
            mContentView.text = note.content
            mTagView.text = note.tag.asTagString(mContext)

            mContentView.visibility = if (note.content.isBlank()) View.GONE else View.VISIBLE

            mDateView.text = when (note.date.diff(Date())) {
                1 -> mContext.getString(R.string.time_tomorrow)
                0 -> mContext.getString(R.string.time_today)
                -1 -> mContext.getString(R.string.time_yesterday)
                else -> note.date.format(mContext.getString(R.string.time_day_month_short_format))
            }

            val wrapper = ContextThemeWrapper(mContext, R.style.BaseTheme_PopupMenuOverlapAnchor)
            val menu = ContextualMenu(mContentView, wrapper, R.menu.item_context)
            menu.onClickListener = this::onMenuItemClicked

            mView.setOnClickListener { menu.show() }
            mView.setOnLongClickListener { true.also { menu.show() } }
        }

        private fun onMenuItemClicked(@IdRes itemId: Int): Boolean {
            when (itemId) {
                R.id.menu_item_edit -> mListener.onEdit(mNote)
                R.id.menu_item_share -> mListener.onShare(mNote)
                R.id.menu_item_delete ->  mListener.onDelete(mNote)
                else -> return false
            }
            return true
        }
    }
}