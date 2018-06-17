package im.mash.moebooru.main.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import im.mash.moebooru.R
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.util.logi

class GalleryDrawerAdapter : RecyclerView.Adapter<GalleryDrawerAdapter.GalleryDrawerViewHolder>() {

    private var items: MutableList<Booru> = mutableListOf()

    fun updateData(items: MutableList<Booru>) {
        this.items = items
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryDrawerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_drawer_sample_item, null)
        return GalleryDrawerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    override fun onBindViewHolder(holder: GalleryDrawerViewHolder, position: Int) {
        if (position == 0) {
            holder.textView.setText(R.string.all_website)
        } else {
            holder.textView.text = items[position-1].host
        }
        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
    }

    private var listener: ItemClickListener? = null

    fun setItemClickListener(listener: ItemClickListener) {
        this.listener = listener
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class GalleryDrawerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text)
    }
}