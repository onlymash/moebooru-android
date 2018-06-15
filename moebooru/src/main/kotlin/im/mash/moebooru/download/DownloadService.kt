package im.mash.moebooru.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.data.local.entity.PostDownload
import im.mash.moebooru.content.UriRetriever
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.viewmodel.DownloadViewModel
import im.mash.moebooru.main.viewmodel.DownloadViewModelFactory
import im.mash.moebooru.util.logi
import java.io.File
import javax.inject.Inject

class DownloadService : LifecycleService() {

    companion object {
        private const val TAG = "DownloadService"

        private var NOTIFICATION_CHANNEL_ID = "im.mash.moebooru.download"

        private const val ACTION_INIT = "init"
        private const val ACTION_START = "start"
        private const val ACTION_STOP = "stop"
        private const val EXTRA_PARAM_START = "start"
        private const val EXTRA_PARAM_STOP = "stop"
        private const val ACTION_START_ALL = "start_all"
        private const val ACTION_STOP_ALL = "stop_all"
        private const val EXTRA_PARAM_START_ALL = "start_all"
        private const val EXTRA_PARAM_STOP_ALL = "stop_all"

        @JvmStatic
        fun init(context: Context) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_INIT
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startTask(context: Context) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_START
            }
            context.startService(intent)
        }

        @JvmStatic
        fun stopTask(context: Context, url: String) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_STOP
                putExtra(EXTRA_PARAM_STOP, url)
            }
            context.startService(intent)
        }
    }

    @Inject
    lateinit var downloadViewModelFactory: DownloadViewModelFactory
    private val downloadViewModel: DownloadViewModel by lazy { downloadViewModelFactory.create(DownloadViewModel::class.java) }

    private val component by lazy { MoeDH.downloadComponent() }

    private var posts: MutableList<PostDownload> = mutableListOf()

    private var startFirst = false

    private lateinit var downloadListener: DownloadListener

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadingNotificationBuilder: NotificationCompat.Builder
    private lateinit var downloadedNotificationBuilder: NotificationCompat.Builder
    private lateinit var downloadErrorNotificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
        initViewModel()
        initNotification()
        initTaskListener()
    }

    private fun initViewModel() {
        downloadViewModel.downloadPostsOutcome.observe(this,
                Observer<Outcome<MutableList<PostDownload>>> { outcome ->
                    when (outcome) {
                        is Outcome.Progress -> {
                            logi(TAG, "Outcome.Progress")
                        }

                        is Outcome.Success -> {
                            posts = outcome.data
                            app.downloadManager.updateData(posts)
                            if (startFirst) {
                                startFirst = false
                                app.downloadManager.start(0)
                            }
                            logi(TAG, "Outcome.Success. posts.size: ${posts.size}")
                        }

                        is Outcome.Failure -> {
                            logi(TAG, "Outcome.Failure")
                        }
                    }
                })
        downloadViewModel.loadAll()
    }

    private fun initTaskListener() {
        downloadListener = DownloadListener()
        downloadListener.setTaskListener(object : DownloadListener.TaskListener {

            override fun onProgress(taskId: Int, fileName: String, currentOffset: Long, totalLength: Long) {
                downloadingNotificationBuilder.setContentText(fileName)
                downloadingNotificationBuilder.setProgress(totalLength.toInt(), currentOffset.toInt(), false)
                notificationManager.notify(taskId, downloadingNotificationBuilder.build())
            }

            override fun onSuccess(taskId: Int, fileName: String, file: File) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(UriRetriever.getUriFromFile(this@DownloadService, file), "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val pendingIntent = PendingIntent.getActivity(this@DownloadService, 0, intent, 0)
                downloadedNotificationBuilder.setContentIntent(pendingIntent)
                downloadedNotificationBuilder.setContentText(fileName)
                notificationManager.notify(taskId, downloadedNotificationBuilder.build())
            }

            override fun onError(taskId: Int, fileName: String, status: String) {
                downloadErrorNotificationBuilder.setContentTitle(status)
                downloadErrorNotificationBuilder.setContentText(fileName)
                notificationManager.notify(taskId, downloadErrorNotificationBuilder.build())
            }

        })
        app.downloadManager.setDownloadListener(downloadListener)
    }

    private fun initNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NOTIFICATION_CHANNEL_ID = "$packageName.download"
            val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    getString(R.string.downloads_notification),
                    NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val stopAllIntent = Intent(this, DownloadService::class.java).apply {
            action = ACTION_STOP_ALL
        }
        val pendingIntentStopAll = PendingIntent.getService(this, 0, stopAllIntent, 0)
        downloadingNotificationBuilder = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setOngoing(true)
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setColor(ContextCompat.getColor(this, R.color.primary_1))
                .addAction(R.drawable.ic_action_pause_primary_24dp, getString(R.string.downloads_stop_all), pendingIntentStopAll)
                .setShowWhen(false)
                .setContentTitle(getString(R.string.stat_downloading_title))
                .setChannelId(NOTIFICATION_CHANNEL_ID)

        downloadedNotificationBuilder = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.stat_download_done_title))
                .setOngoing(false)
                .setAutoCancel(true)
                .setChannelId(NOTIFICATION_CHANNEL_ID)

        downloadErrorNotificationBuilder = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setOngoing(false)
                .setAutoCancel(true)
                .setChannelId(NOTIFICATION_CHANNEL_ID)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_START -> {
                handleActionStart()
            }
            ACTION_STOP -> {
                val param = intent.getStringExtra(EXTRA_PARAM_STOP)
                handleActionStop(param)
            }
            ACTION_START_ALL -> {
                app.downloadManager.startAll(true)
            }
            ACTION_STOP_ALL -> {
                app.downloadManager.stopAll()
            }
        }
    }

    private fun handleActionStart() {
        startFirst = true
    }

    private fun handleActionStop(param: String) {

    }

    override fun onDestroy() {
        super.onDestroy()
        app.downloadManager.setDownloadListener(null)
    }
}
