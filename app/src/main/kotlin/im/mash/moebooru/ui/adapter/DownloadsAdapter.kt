package im.mash.moebooru.ui.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.load.model.GlideUrl
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.download.MoeDownloadController
import im.mash.moebooru.ui.widget.FixedImageView
import im.mash.moebooru.download.MoeStatusChangeListener
import im.mash.moebooru.download.ProgressUtil
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.model.DownloadPost
import im.mash.moebooru.utils.glideHeader
import java.time.Instant

class DownloadsAdapter(private val controller: MoeDownloadController,
                       private var size: Int) : RecyclerView.Adapter<DownloadsAdapter.DownloadsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadsViewHolder {
        val itemsView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_downloads_item, parent, false)
        return DownloadsViewHolder(itemsView)
    }

    fun updateSize(size: Int) {
        this.size = size
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onBindViewHolder(holder: DownloadsViewHolder, position: Int) {
        controller.bind(holder.listener, position)
        holder.start.setOnClickListener {
            controller.start(position)
        }
        holder.itemView.setOnClickListener {
            val uri = controller.getPostsUriFromPosition(position)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            try {
                app.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class DownloadsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), MoeStatusChangeListener {

        val preview: FixedImageView = itemView.findViewById(R.id.preview)
        val title: TextView = itemView.findViewById(R.id.title)
        val status: TextView = itemView.findViewById(R.id.status)
        val start: ImageView = itemView.findViewById(R.id.start)
        val stop: ImageView = itemView.findViewById(R.id.stop)
        val percent: TextView = itemView.findViewById(R.id.percent)
        val speed: TextView = itemView.findViewById(R.id.speed)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val listener: MoeStatusChangeListener = this

        override fun onSetTitle(title: String) {
            this.title.text = title
        }

        override fun onStatusChanged(status: String) {
            this.status.text = status
        }

        override fun onStatusChanged(statusId: Int) {
            this.status.setText(statusId)
        }

        override fun onProgressNotStart() {
            this.progressBar.progress = 0
        }

        override fun onProgressStart(currentOffset: Long, totalLength: Long) {
            ProgressUtil.calcProgressToViewAndMark(this.progressBar,
                    currentOffset, totalLength, false)
        }

        override fun onProgressChange(currentOffset: Long) {
            ProgressUtil.updateProgressToViewWithMark(this.progressBar,
                    currentOffset, false)
        }

        override fun onProgressCompleted() {
            this.progressBar.progress = this.progressBar.max
        }

        override fun onLoadPreview(url: String) {
            GlideApp.with(app)
                    .load(GlideUrl(url, glideHeader))
                    .centerCrop()
                    .into(this.preview)
        }
    }
}