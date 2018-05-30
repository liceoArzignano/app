package it.liceoarzignano.curve.pings

import it.liceoarzignano.curve.MainActivity
import it.liceoarzignano.foundation.db.entities.Ping
import it.liceoarzignano.foundation.pings.PingsFragment

class CurvePingsFragment : PingsFragment() {

    override fun onItemClick(item: Ping) {
        if (item.url.isEmpty()) {
            return
        }

        if (activity is MainActivity) {
            val mainAct = activity as MainActivity
            mainAct.openCustomTab(item.url)
        }
    }
}