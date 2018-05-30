package it.liceoarzignano.foundation.util

import androidx.recyclerview.widget.DiffUtil

class DiffCallback<in T>(private val oldList: List<T>,
                         private val newList: List<T>) : DiffUtil.Callback() {
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition]!!.hashCode() == newList[newItemPosition]!!.hashCode()

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size
}