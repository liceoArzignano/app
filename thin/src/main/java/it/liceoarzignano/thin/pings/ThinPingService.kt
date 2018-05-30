package it.liceoarzignano.thin.pings

import android.content.Intent
import it.liceoarzignano.foundation.pings.PingService
import it.liceoarzignano.thin.MainActivity
import it.liceoarzignano.thin.R

class ThinPingService : PingService() {

    override val notificationColor = R.color.colorAccentDark

    override fun getNotificationIntent(): Intent {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PING, ping)
        return intent
    }
}
