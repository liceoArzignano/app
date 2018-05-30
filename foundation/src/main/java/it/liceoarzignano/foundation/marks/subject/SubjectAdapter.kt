package it.liceoarzignano.foundation.marks.subject

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.liceoarzignano.foundation.R
import it.liceoarzignano.foundation.db.entities.Mark
import it.liceoarzignano.foundation.extensions.asMarkStr
import it.liceoarzignano.foundation.extensions.format
import it.liceoarzignano.foundation.extensions.getColorAttr
import it.liceoarzignano.foundation.util.DiffCallback
import it.liceoarzignano.foundation.util.OnContextMenuClickedListener
import it.liceoarzignano.foundation.widgets.ContextualMenu

class SubjectAdapter(private val mContext: Context,
                     private val mListener: OnContextMenuClickedListener<Mark>) :
        RecyclerView.Adapter<SubjectAdapter.SubjectHolder>() {
    var list: List<Mark> = emptyList()
        set(value) {
            val diff = DiffUtil.calculateDiff(DiffCallback(list, value))
            field = value
            diff.dispatchUpdatesTo(this)
        }

    private val mBadColor = ContextCompat.getColor(mContext, R.color.negative)
    private val mNeutralColor = mContext.getColorAttr(R.style.BaseTheme,
            android.R.attr.textColorSecondary)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            SubjectHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_mark, parent, false))

    override fun onBindViewHolder(holder: SubjectHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class SubjectHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {
        private val mValueView = mView.findViewById<TextView>(R.id.item_mark_value)
        private val mDateView = mView.findViewById<TextView>(R.id.item_mark_date)
        private val mNotesView = mView.findViewById<TextView>(R.id.item_mark_notes)

        private lateinit var mMark: Mark

        fun bind(mark: Mark) {
            mMark = mark

            val value = mark.value.asMarkStr()
            mValueView.text = value
            mValueView.setTextColor(if (mark.value < 6f) mBadColor else mNeutralColor)

            val date = mark.date.format(mContext.getString(R.string.editor_date_format))
            mDateView.text = date

            val notes = mark.notes
            if (notes.isNotEmpty()) {
                mNotesView.text = notes
                mNotesView.visibility = View.VISIBLE
            }


            val wrapper = ContextThemeWrapper(mContext, R.style.BaseTheme_PopupMenuOverlapAnchor)
            val menu = ContextualMenu(mDateView, wrapper, R.menu.item_context)
            menu.onClickListener = this::onMenuItemClicked

            mView.setOnClickListener { menu.show() }
            mView.setOnLongClickListener { true.also { menu.show() } }
        }

        private fun onMenuItemClicked(@IdRes itemId: Int): Boolean {
            when (itemId) {
                R.id.menu_item_edit -> mListener.onEdit(mMark)
                R.id.menu_item_share -> mListener.onShare(mMark)
                R.id.menu_item_delete ->  mListener.onDelete(mMark)
                else -> return false
            }
            return true
        }
    }
}