package im.mash.moebooru.download

import android.net.Uri
import android.util.Log
import com.liulishuo.okdownload.DownloadContext
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.DownloadContextListener
import com.liulishuo.okdownload.core.cause.EndCause
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.content.UriRetriever.getUriFromFilePath
import im.mash.moebooru.content.moebooruDir
import im.mash.moebooru.model.DownloadPost
import im.mash.moebooru.utils.okDownloadHeaders
import java.io.File
import java.lang.Exception
import java.net.URLDecoder
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

    private lateinit var posts: MutableList<DownloadPost>
    private lateinit var downloadListener: MoeDownloadListener

    private var tasks: MutableList<DownloadTask> = mutableListOf()
    private var size = 0

    private lateinit var downloadContext: DownloadContext
    private lateinit var builder: DownloadContext.Builder

    private fun initTask() {
        size = posts.size
        if (size == 0) {
            return
        }
        tasks.clear()
        for (index in 0 until size) {
            val url = posts[size - index - 1].url
            val fileName: String = URLDecoder.decode(url.substring(url.lastIndexOf("/") + 1), "UTF-8")
            val booruPath = moebooruDir.absolutePath + "/" + posts[size - index - 1].domain
            val booruDir = File(booruPath)
            if (!booruDir.exists()) {
                if (!booruDir.mkdirs()) {
                    Log.i(TAG, "Directory not created")
                }
            }
            val boundTask: DownloadTask = DownloadTask.Builder(url, booruDir).setFilename(fileName).build()
            tasks.add(boundTask)
            TagUtil.saveTaskName(boundTask, posts[size - index - 1].id.toString()
                    + " - " + posts[size - index - 1].domain)
        }
        val set: DownloadContext.QueueSet = DownloadContext.QueueSet()
        set.minIntervalMillisCallbackProcess = 100
        set.headerMapFields = okDownloadHeaders
        builder = DownloadContext.Builder(set, tasks as ArrayList<DownloadTask>?)
        builder.setListener(this)
        downloadContext = builder.build()
    }

    private fun getPostUri(url: String, domain: String): Uri {
        val fileName: String = URLDecoder.decode(url.substring(url.lastIndexOf("/") + 1), "UTF-8")
        val booruPath = moebooruDir.absolutePath + "/" + domain
        val booruDir = File(booruPath)
        if (!booruDir.exists()) {
            if (!booruDir.mkdirs()) {
                Log.i(TAG, "Directory not created")
            }
        }
        return getUriFromFilePath(app, "$booruPath/$fileName")
    }

    fun getPostsUriFromPosition(position: Int): Uri {
        val url = posts[size - position - 1].url
        return getPostUri(url, posts[size - position - 1].domain)
    }

    fun updateData(posts: MutableList<DownloadPost>) {
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
        tasks.forEach { task ->
            TagUtil.clearProceedTask(task)
            task.file?.delete()
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

        listener.onLoadPreview(posts[size - position - 1].preview_url)
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