package it.liceoarzignano.foundation.util

interface OnContextMenuClickedListener<T> {

    fun onEdit(item: T)
    fun onDelete(item: T)
    fun onShare(item: T)
}