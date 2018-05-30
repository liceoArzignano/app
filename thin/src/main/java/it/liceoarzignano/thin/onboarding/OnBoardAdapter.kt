package it.liceoarzignano.thin.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.liceoarzignano.thin.R

class OnBoardAdapter(private val onItemClicked: (String, Int) -> Unit) :
        RecyclerView.Adapter<OnBoardAdapter.OnBoardViewHolder>() {
    var list: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            OnBoardViewHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_onboard_answer, parent, false))

    override fun onBindViewHolder(holder: OnBoardViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = list.size

    inner class OnBoardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mAnswerView = view.findViewById<TextView>(R.id.item_onboard_answer)

        fun bind(position: Int) {
            mAnswerView.text = list[position]
            mAnswerView.setOnClickListener { onItemClicked(list[position], position) }
        }
    }
}