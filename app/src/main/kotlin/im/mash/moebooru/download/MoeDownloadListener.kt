package im.mash.moebooru.download

import android.content.Intent
import android.net.Uri
import android.util.SparseArray
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.StatusUtil
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import java.lang.Exception

class MoeDownloadListener : DownloadListener1() {

    companion object {
        private const val TAG = "MoeDownloadListener"
    }

    private var listeners: SparseArray<MoeStatusChangeListener> = SparseArray()


    fun bind(task: DownloadTask, listener: MoeStatusChangeListener) {
        val size = listeners.size()
        for (index in 0 until size) {
            if (listeners[index] == listener) {
                listeners.removeAt(index)
                break
            }
        }
        listeners.put(task.id, listener)
    }

    fun clearBoundHolder() {
        listeners.clear()
    }

    fun resetInfo(task: DownloadTask, listener: MoeStatusChangeListener) {
        // task name
        val taskName = TagUtil.getTaskName(task)
        if (taskName != null) {
            listener.onSetTitle(taskName)
        }
        // process references
        val status = TagUtil.getStatus(task)
        if (status != null) {
            listener.onStatusChanged(status)
            if (status == EndCause.COMPLETED.toString()) {
                listener.onProgressCompleted()
            } else {
                val total = TagUtil.getTotal(task)
                if (total == 0L) {
                    listener.onProgressNotStart()
                } else {
                    listener.onProgressStart(TagUtil.getOffset(task), total)
                }
            }
        } else {
            // non-started
            val statusOnStore: StatusUtil.Status = StatusUtil.getStatus(task)
            TagUtil.saveStatus(task, statusOnStore.toString())
            if (statusOnStore == StatusUtil.Status.COMPLETED) {
                listener.onStatusChanged(EndCause.COMPLETED.toString())
                listener.onProgressCompleted()
            } else {
                when (statusOnStore) {
                    StatusUtil.Status.IDLE -> listener.onStatusChanged(R.string.state_idle)
                    StatusUtil.Status.PENDING -> listener.onStatusChanged(R.string.state_pending)
                    StatusUtil.Status.RUNNING -> listener.onStatusChanged(R.string.state_running)
                    else -> listener.onStatusChanged(R.string.state_unknown)
                }
                if (statusOnStore == StatusUtil.Status.UNKNOWN) {
                    listener.onProgressNotStart()
                } else {
                    val info = StatusUtil.getCurrentInfo(task)
                    if (info != null) {
                        TagUtil.saveTotal(task, info.totalLength)
                        TagUtil.saveOffset(task, info.totalOffset)
                        listener.onProgressStart(info.totalOffset, info.totalLength)
                    } else {
                        listener.onProgressNotStart()
                    }
                }
            }
        }
    }

    override fun taskStart(task: DownloadTask, model: Listener1Assist.Listener1Model) {
        val status = "taskStart"
        TagUtil.saveStatus(task, status)
        val listener = listeners.get(task.id) ?: return
        listener.onStatusChanged(status)
    }

    override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?, model: Listener1Assist.Listener1Model) {
        if (cause == EndCause.COMPLETED) {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(task.file))
            app.sendBroadcast(mediaScanIntent)
        }
        val status = cause.toString()
        TagUtil.saveStatus(task, status)
        val listener = listeners.get(task.id) ?: return
        listener.onStatusChanged(status)
        if (cause == EndCause.COMPLETED) {
            listener.onProgressCompleted()
        }
    }

    override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
        val status = "progress"
        TagUtil.saveStatus(task, status)
        TagUtil.saveOffset(task, currentOffset)
        val listener = listeners.get(task.id) ?: return
        listener.onStatusChanged(status)
        listener.onProgressChange(currentOffset)
    }

    override fun connected(task: DownloadTask, blockCount: Int, currentOffset: Long, totalLength: Long) {
        val status = "connected"
        TagUtil.saveStatus(task, status)
        TagUtil.saveOffset(task, currentOffset)
        TagUtil.saveTotal(task, totalLength)
        val listener = listeners.get(task.id) ?: return
        listener.onStatusChanged(status)
        listener.onProgressStart(currentOffset, totalLength)
    }

    override fun retry(task: DownloadTask, cause: ResumeFailedCause) {
        val status = "retry"
        TagUtil.saveStatus(task, status)
        val listener = listeners.get(task.id) ?: return
        listener.onStatusChanged(status)
    }
}