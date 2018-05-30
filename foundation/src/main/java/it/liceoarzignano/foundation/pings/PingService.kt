package it.liceoarzignano.foundation.pings

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import it.liceoarzignano.foundation.BuildConfig
import it.liceoarzignano.foundation.R
import it.liceoarzignano.foundation.db.AppDatabase
import it.liceoarzignano.foundation.db.DatabaseTask
import it.liceoarzignano.foundation.db.entities.Ping
import it.liceoarzignano.foundation.extensions.get
import it.liceoarzignano.foundation.util.PreferenceKeys
import org.json.JSONObject

abstract class PingService : FirebaseMessagingService() {
    protected lateinit var ping: Ping

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (remoteMessage == null || remoteMessage.data.isEmpty()) {
            return
        }

        val json = JSONObject(remoteMessage.data.toString())
        val title = json.getString(if (!BuildConfig.DEBUG) "d_title" else "title")
        val message = json.getString(if (!BuildConfig.DEBUG) "d_message" else "message")
        val url = json.getString("url")
        val isPrivate = json.getBoolean("isPrivate")

        if (isPrivate && !acceptsPrivateMessages()) {
            return
        }

        ping = Ping()
        ping.name = title
        ping.message = message
        ping.url = url

        savePing()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs[PreferenceKeys.KEY_NOTIF_PING, true]) {
            showNotification()
        }
    }

    protected abstract fun getNotificationIntent(): Intent

    protected abstract val notificationColor: Int

    private fun acceptsPrivateMessages(): Boolean {
        return false
    }

    private fun savePing() {
        val db = AppDatabase.getInstance(baseContext)
        SavePingTask(db).execute(ping)
    }

    private fun showNotification() {
        val intent = getNotificationIntent()
        val pIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(baseContext, CHANNEL_NAME)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(baseContext, notificationColor))
                .setContentText(ping.message)
                .setContentTitle(ping.name)
                .setGroup(CHANNEL_NAME)
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.ic_notification)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            prepareChannel(manager)
        }

        val timestamp = System.currentTimeMillis()
        val id = (timestamp - ((timestamp / 1000000) * 1000000)).toInt()

        manager.notify(id, builder.build())
    }

    @TargetApi(26)
    private fun prepareChannel(manager: NotificationManager) {
        var channel = manager.getNotificationChannel(CHANNEL_NAME)
        if (channel != null) {
            return
        }

        val title = getString(R.string.notification_channel_pings_title)
        channel = NotificationChannel(CHANNEL_NAME, title, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = getString(R.string.notification_channel_pings_description)
        channel.enableLights(true)
        manager.createNotificationChannel(channel)
    }


    class SavePingTask(db: AppDatabase) : DatabaseTask<Ping, Unit>(db) {
        override fun doInBackground(vararg params: Ping) {
            db.pings().insert(params[0])
        }
    }

    companion object {
        private const val CHANNEL_NAME = "pings_channel"
    }
}