package im.mash.moebooru.download

import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.Context
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.data.local.entity.PostDownload
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.viewmodel.DownloadViewModel
import im.mash.moebooru.main.viewmodel.DownloadViewModelFactory
import im.mash.moebooru.util.logi
import javax.inject.Inject

class DownloadService : LifecycleService() {

    companion object {
        private const val TAG = "DownloadService"

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

    private var downloadListener: DownloadListener = DownloadListener()

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
        initViewModel()
        app.downloadManager.setDownloadListener(downloadListener)
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

            }
            ACTION_STOP_ALL -> {

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
