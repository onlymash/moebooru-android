package im.mash.moebooru.download

import android.content.Context
import com.liulishuo.okdownload.DownloadContext
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.DownloadContextListener
import com.liulishuo.okdownload.core.cause.EndCause
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.content.booruDir
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.utils.Key
import java.io.File
import java.lang.Exception
import java.util.ArrayList

class MoeDownloadController : DownloadContextListener {

    companion object {
        private const val TAG = "DownloadController"
        private var instance: MoeDownloadController? = null
        @Synchronized
        fun getInstance() : MoeDownloadController {
            if (instance == null) {
                instance = MoeDownloadController()
            }
            return instance!!
        }
    }

    private lateinit var posts: MutableList<RawPost>
    private lateinit var downloadListener: MoeDownloadListener

    private var tasks: MutableList<DownloadTask> = mutableListOf()
    private var parentDir: File? = null
    private var size = 0

    private lateinit var downloadContext: DownloadContext

    private fun initTask() {
        val set: DownloadContext.QueueSet = DownloadContext.QueueSet()
        parentDir = booruDir
        set.setParentPathFile(parentDir!!)
        set.minIntervalMillisCallbackProcess = 200
        initData()
        val builder: DownloadContext.Builder = DownloadContext.Builder(set, tasks as ArrayList<DownloadTask>?)
        builder.setListener(this)
        downloadContext = builder.build()
    }

    private fun initData() {
        val data = app.downloadManager.loadPosts(app.settings.activeProfile)
        if (data != null) {
            posts = data
        }
        size = posts.size
        when (app.settings.postSizeDownload) {
            Key.POST_SIZE_SAMPLE -> {
                for (index in 0 until size) {
                    val url = posts[size - index - 1].sample_url!!
                    val fileName = url.substring(url.lastIndexOf("/") + 1).replace("%20", " ")
                    val boundTask: DownloadTask = DownloadTask.Builder(url, parentDir!!)
                            .setFilename(fileName)
                            .build()
                    tasks.add(boundTask)
                    TagUtil.saveTaskName(boundTask, posts[size - index - 1].id.toString())
                }
            }
            Key.POST_SIZE_LARGER -> {
                for (index in 0 until size) {
                    val url = posts[size - index - 1].jpeg_url!!
                    val fileName = url.substring(url.lastIndexOf("/") + 1).replace("%20", " ")
                    val boundTask: DownloadTask = DownloadTask.Builder(url, parentDir!!)
                            .setFilename(fileName)
                            .build()
                    tasks.add(boundTask)
                    TagUtil.saveTaskName(boundTask, posts[size - index - 1].id.toString())
                }
            }
            else -> {
                for (index in 0 until size) {
                    val url = posts[size - index - 1].file_url!!
                    val fileName = url.substring(url.lastIndexOf("/") + 1).replace("%20", " ")
                    val boundTask: DownloadTask = DownloadTask.Builder(url, parentDir!!)
                            .setFilename(fileName)
                            .build()
                    tasks.add(boundTask)
                    TagUtil.saveTaskName(boundTask, posts[size - index - 1].id.toString())
                }
            }
        }
    }

    fun updateData(posts: MutableList<RawPost>) {
        this.posts = posts
        initTask()
    }

    fun startAll(isSerial: Boolean) {
        downloadContext.start(downloadListener, isSerial)
    }

    fun stopAll() {
        if (downloadContext.isStarted) {
            downloadContext.stop()
        }
    }

    fun start(position: Int) {
        tasks[position].enqueue(downloadListener)
    }

    fun stop(position: Int) {
        tasks[position].cancel()
    }

    fun deleteFiles() {
        if (parentDir != null) {
            parentDir!!.list()?.forEach { child ->
                File(parentDir, child).delete()
            }
            parentDir!!.delete()
        }
        tasks.forEach { task ->
            TagUtil.clearProceedTask(task)
        }
    }

    fun setPriority(task: DownloadTask, priority: Int) {
        val newTask = task.toBuilder().setPriority(priority).build()
        downloadContext = downloadContext.toBuilder()
                .bindSetTask(newTask)
                .build()
        newTask.setTags(task)
        TagUtil.savePriority(newTask, priority)
        tasks = downloadContext.tasks.toMutableList()
    }

    fun bind(listener: MoeStatusChangeListener, position: Int) {
        val task = tasks[position]
        downloadListener.bind(task, listener)
        downloadListener.resetInfo(task, listener)
        // priority
        val priority = TagUtil.getPriority(task)

        listener.onLoadPreview(posts[size - position - 1].preview_url!!)
    }

    fun getCount(): Int = tasks.size

    fun setDownloadListener(downloadListener: MoeDownloadListener) {
        this.downloadListener = downloadListener
    }

    override fun taskEnd(context: DownloadContext, task: DownloadTask, cause: EndCause,
                         realCause: Exception?, remainCount: Int) {

    }

    override fun queueEnd(context: DownloadContext) {

    }
}