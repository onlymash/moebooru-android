package im.mash.moebooru.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import im.mash.moebooru.R

class DownloadsAdapter : RecyclerView.Adapter<DownloadsAdapter.DownloadsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadsViewHolder {
        val itemsView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_downloads_item, parent, false)
        return DownloadsViewHolder(itemsView)
    }

    override fun getItemCount(): Int {
        return 15
    }

    override fun onBindViewHolder(holder: DownloadsViewHolder, position: Int) {
        holder.title.text = position.toString()
    }

    inner class DownloadsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)

    }
}