package it.liceoarzignano.foundation.widgets

import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.liceoarzignano.foundation.util.MainAdapter

abstract class MainFragment<T, A : MainAdapter<T, *>> : Fragment() {
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var emptyView: TextView

    protected lateinit var adapter: A

    protected open val adapterLayoutManager: RecyclerView.LayoutManager
        get() = LinearLayoutManager(context)

    fun update(list: List<T>?) {
        adapter.list = list ?: emptyList()

        if (adapter.list.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    protected fun setupRecyclerView() {
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = adapterLayoutManager
        recyclerView.adapter = adapter
    }

    abstract fun onItemClick(item: T)

    abstract fun shouldShowFab(): Boolean

    abstract fun onFabClicked()
}