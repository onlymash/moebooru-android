package im.mash.moebooru.download

import android.arch.lifecycle.LifecycleService
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.Context
import android.util.Log
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.data.local.entity.PostDownload
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.viewmodel.DownloadViewModel
import im.mash.moebooru.main.viewmodel.DownloadViewModelFactory
import javax.inject.Inject

class DownloadService : LifecycleService(){

    companion object {
        private const val TAG = "DownloadService"

        private const val ACTION_START = "start"
        private const val ACTION_STOP = "stop"
        private const val EXTRA_PARAM_START = "start"
        private const val EXTRA_PARAM_STOP = "stop"

        private const val ACTION_START_ALL = "start_all"
        private const val ACTION_STOP_ALL = "stop_all"
        private const val EXTRA_PARAM_START_ALL = "start_all"
        private const val EXTRA_PARAM_STOP_ALL = "stop_all"

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
    lateinit var downloadManager: DownloadManager
    @Inject
    lateinit var downloadViewModelFactory: DownloadViewModelFactory
    private val downloadViewModel: DownloadViewModel by lazy { downloadViewModelFactory.create(DownloadViewModel::class.java) }

    private val component by lazy { MoeDH.downloadComponent() }

    private var posts: MutableList<PostDownload> = mutableListOf()

    private var status: String = ""

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
        initViewModel()
    }

    private fun initViewModel() {
        downloadViewModel.downloadPostsOutcome.observe(this,
                Observer<Outcome<MutableList<PostDownload>>> { outcome ->
                    when (outcome) {
                        is Outcome.Progress -> {
                            Log.i(TAG, "Outcome.Progress")
                        }

                        is Outcome.Success -> {
                            posts = outcome.data
                            Log.i(TAG, "Outcome.Success. posts.size: ${posts.size}")
                        }

                        is Outcome.Failure -> {
                            Log.i(TAG, "Outcome.Failure")
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
        status = ACTION_START
    }

    private fun handleActionStop(param: String) {
        status = ACTION_STOP
    }

}
