package im.mash.moebooru.download

import android.app.IntentService
import android.content.Intent
import android.content.Context

class DownloadService : IntentService("DownloadService") {

    companion object {

        private const val ACTION_START = "start"
        private const val ACTION_STOP = "stop"
        private const val EXTRA_PARAM_START = "start"
        private const val EXTRA_PARAM_STOP = "stop"

        private const val ACTION_START_ALL = "start_all"
        private const val ACTION_STOP_ALL = "stop_all"
        private const val EXTRA_PARAM_START_ALL = "start_all"
        private const val EXTRA_PARAM_STOP_ALL = "stop_all"

        @JvmStatic
        fun startTask(context: Context, param: String) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_PARAM_START, param)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun stopTask(context: Context, param: String) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_STOP
                putExtra(EXTRA_PARAM_STOP, param)
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_START -> {
                val param = intent.getStringExtra(EXTRA_PARAM_START)
                handleActionStart(param)
            }
            ACTION_STOP -> {
                val param = intent.getStringExtra(EXTRA_PARAM_STOP)
                handleActionStop(param)
            }
            ACTION_START_ALL -> {

            }
            ACTION_STOP_ALL -> {

            }
        }
    }

    private fun handleActionStart(param: String) {

    }

    private fun handleActionStop(param: String) {

    }

}
