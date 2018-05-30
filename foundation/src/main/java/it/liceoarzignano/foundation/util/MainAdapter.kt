package it.liceoarzignano.foundation.util

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class MainAdapter <T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    var list: List<T> = emptyList()
        set(value) {
            val diff = DiffUtil.calculateDiff(DiffCallback(list, value))
            field = value
            diff.dispatchUpdatesTo(this)
        }

    override fun getItemCount() = list.size
}