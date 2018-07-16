package im.mash.moebooru.detail.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import im.mash.moebooru.R

class TagAdapter : RecyclerView.Adapter<TagAdapter.TagViewHolder>(){

    private var tags: MutableList<String> = mutableListOf()

    fun updateData(tags: MutableList<String>) {
        this.tags = tags
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_details_tags_item, parent, false)
        return TagViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.tagName.text = tags[position]
        holder.itemView.setOnClickListener {
            listener?.onClickItem(tags[position])
        }
        holder.itemView.setOnLongClickListener {
            listener?.onLongClickItem(tags[position])
            return@setOnLongClickListener true
        }
        holder.tagAdd.setOnClickListener {
            listener?.onClickAdd(tags[position])
        }
        holder.tagAddNegated.setOnClickListener {
            listener?.onClickAddNegated(tags[position])
        }
    }

    private var listener: TagItemClickListener? = null

    interface TagItemClickListener {
        fun onClickItem(tag: String)
        fun onClickAdd(tag: String)
        fun onClickAddNegated(tag: String)
        fun onLongClickItem(tag: String)
    }

    fun setTagItemClickListener(listener: TagItemClickListener) {
        this.listener = listener
    }

    inner class TagViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tagName = itemView.findViewById<TextView>(R.id.tag_name)!!
        val tagAddNegated = itemView.findViewById<ImageButton>(R.id.tag_add_negated)!!
        val tagAdd = itemView.findViewById<ImageButton>(R.id.tag_add)!!
    }
}