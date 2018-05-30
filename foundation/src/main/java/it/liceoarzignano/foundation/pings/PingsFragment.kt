package it.liceoarzignano.foundation.pings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.liceoarzignano.foundation.R
import it.liceoarzignano.foundation.db.entities.Ping
import it.liceoarzignano.foundation.widgets.MainFragment

abstract class PingsFragment : MainFragment<Ping, PingsAdapter>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_pings, container, false)

        recyclerView = view.findViewById(R.id.fragment_pings_list)
        emptyView = view.findViewById(R.id.fragment_pings_empty)

        if (context != null) {
            adapter = PingsAdapter(context!!, this::onItemClick)
            setupRecyclerView()
        }

        return view
    }

    override fun shouldShowFab() = false
    override fun onFabClicked() = Unit
}
