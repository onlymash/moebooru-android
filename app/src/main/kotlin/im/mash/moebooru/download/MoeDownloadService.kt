package im.mash.moebooru.download

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R

class MoeDownloadService : IntentService("MoeDownloadService") {

    companion object {
        private var NOTIFICATION_CHANNEL_ID = "im.mash.moebooru.download"
        private const val ACTION_START = "start"
        private const val ACTION_STOP = "stop"
        private const val ACTION_START_ALL = "start_all"
        private const val ACTION_STOP_ALL = "stop_all"
    }

    private var notificationManager: NotificationManager? = null


    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NOTIFICATION_CHANNEL_ID = "$packageName.download"
            val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    getString(R.string.downloads_notification),
                    NotificationManager.IMPORTANCE_LOW)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
        app.downloadController.setDownloadListener(MoeDownloadListener())

    }
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        val action: String = intent.action
        when (action) {
            ACTION_START -> {

            }
            ACTION_STOP -> {

            }
            ACTION_START_ALL -> {
                app.downloadController.startAll(true)
            }
            ACTION_STOP_ALL -> {
                app.downloadController.stopAll()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager = null
    }
}
