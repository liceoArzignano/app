package it.liceoarzignano.thin.pings

import it.liceoarzignano.foundation.db.entities.Ping
import it.liceoarzignano.foundation.pings.PingsFragment
import it.liceoarzignano.thin.MainActivity

class ThinPingsFragment : PingsFragment() {

    override fun onItemClick(item: Ping)  {
        if (item.url.isEmpty()) {
            return
        }

        if (activity is MainActivity) {
            val mainAct = activity as MainActivity
            mainAct.openCustomTab(item.url)
        }
    }
}