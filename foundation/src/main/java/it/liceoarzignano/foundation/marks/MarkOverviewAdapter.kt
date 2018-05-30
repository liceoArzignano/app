package it.liceoarzignano.foundation.marks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.liceoarzignano.foundation.R
import it.liceoarzignano.foundation.db.entities.Mark
import it.liceoarzignano.foundation.util.MainAdapter
import it.liceoarzignano.foundation.widgets.CircularProgressBar

class MarkOverviewAdapter(private val onItemClick: (Mark) -> Unit) :
        MainAdapter<Mark, MarkOverviewAdapter.MarkOverviewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            MarkOverviewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_subject, parent, false))

    override fun onBindViewHolder(holder: MarkOverviewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class MarkOverviewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {
        private val mNameView = mView.findViewById<TextView>(R.id.item_subject_name)
        private val mValueView = mView.findViewById<CircularProgressBar>(R.id.item_subject_value)

        fun bind(mark: Mark) {
            mNameView.text = mark.subject
            mValueView.setProgress(mark.value.toDouble())

            mView.setOnClickListener {
                onItemClick(mark)
            }
        }
    }
}
