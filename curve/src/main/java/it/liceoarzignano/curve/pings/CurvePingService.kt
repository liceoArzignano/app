package it.liceoarzignano.curve.pings

import android.content.Intent
import it.liceoarzignano.curve.MainActivity
import it.liceoarzignano.curve.R
import it.liceoarzignano.foundation.pings.PingService

class CurvePingService : PingService() {

    override val notificationColor = R.color.colorAccentDark

    override fun getNotificationIntent(): Intent {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PING, ping)
        return intent
    }
}