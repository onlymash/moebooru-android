package im.mash.moebooru.main.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import im.mash.moebooru.R
import im.mash.moebooru.common.data.local.entity.PostDownload
import im.mash.moebooru.core.widget.FixedImageView
import im.mash.moebooru.download.DownloadManager
import im.mash.moebooru.download.DownloadStatusListener
import im.mash.moebooru.download.ProgressUtil
import im.mash.moebooru.glide.GlideApp

class DownloadAdapter(private val context: Context,
                      private val downloadManager: DownloadManager) : RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>()  {

    private var posts: MutableList<PostDownload> = mutableListOf()

    fun updateData(posts: MutableList<PostDownload>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        val itemsView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_downloads_item, parent, false)
        return DownloadViewHolder(itemsView, context)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        downloadManager.bind(holder.listener, position)
        holder.start.setOnClickListener {
            downloadManager.start(position)
        }
        holder.stop.setOnClickListener {
            downloadManager.stop(position)
        }
    }

    class DownloadViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView), DownloadStatusListener {

        val preview: FixedImageView = itemView.findViewById(R.id.preview)
        val title: TextView = itemView.findViewById(R.id.title)
        val status: TextView = itemView.findViewById(R.id.status)
        val start: ImageView = itemView.findViewById(R.id.start)
        val stop: ImageView = itemView.findViewById(R.id.stop)
        val percent: TextView = itemView.findViewById(R.id.percent)
        val speed: TextView = itemView.findViewById(R.id.speed)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val listener: DownloadStatusListener = this

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
            GlideApp.with(context)
                    .load(url)
                    .centerCrop()
                    .into(this.preview)
        }

        override fun onStart() {
            start.visibility = View.INVISIBLE
            stop.visibility = View.VISIBLE
        }

        override fun onEnd() {
            stop.visibility = View.INVISIBLE
            start.visibility = View.VISIBLE
        }
    }
}