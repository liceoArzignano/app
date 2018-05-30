package it.liceoarzignano.foundation.pings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.liceoarzignano.foundation.R
import it.liceoarzignano.foundation.db.entities.Ping
import it.liceoarzignano.foundation.extensions.diff
import it.liceoarzignano.foundation.extensions.getHeader
import it.liceoarzignano.foundation.util.MainAdapter

class PingsAdapter(private val mContext: Context,
                   private val onItemClick: (Ping) -> Unit) :
        MainAdapter<Ping, PingsAdapter.PingHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            PingHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_ping, parent, false))

    override fun onBindViewHolder(holder: PingHolder, position: Int) {
        holder.bind(position)
    }

    private fun shouldInsertHeader(position: Int): Boolean {
        if (list.isEmpty()) {
            return false
        }
        if (position == 0) {
            return true
        }

        val a = list[position - 1].date
        val b = list[position].date

        return a.diff(b) > 0
    }

    inner class PingHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {
        private val mHeaderLayout = mView.findViewById<View>(R.id.item_ping_header)
        private val mHeaderTitle = mView.findViewById<TextView>(R.id.item_header_title)
        private val mHeaderDate = mView.findViewById<TextView>(R.id.item_header_date)
        private val mTitleText = mView.findViewById<TextView>(R.id.item_ping_title)
        private val mContentText = mView.findViewById<TextView>(R.id.item_ping_message)

        fun bind(position: Int) {
            val ping = list[position]

            // Header
            val shouldShowHeader = shouldInsertHeader(position)
            if (shouldShowHeader) {
                val headerContent = ping.date.getHeader(mContext.resources)
                mHeaderTitle.text = headerContent.first
                mHeaderDate.text = headerContent.second
                mHeaderLayout.visibility = View.VISIBLE
            }

            mTitleText.text = ping.name
            mContentText.text = ping.message

            mView.setOnClickListener { onItemClick(ping) }
        }
    }
}
