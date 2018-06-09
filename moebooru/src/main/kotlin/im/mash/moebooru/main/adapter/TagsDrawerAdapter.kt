package im.mash.moebooru.main.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import im.mash.moebooru.R
import im.mash.moebooru.common.data.local.entity.Tag

class TagsDrawerAdapter(private val context: Context) : RecyclerView.Adapter<TagsDrawerAdapter.TagsDrawerViewHolder>()  {

    private var tags = mutableListOf<Tag>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsDrawerViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_drawer_tags_item, parent, false)
        return TagsDrawerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    fun updateData(tags: MutableList<Tag>) {
        this.tags = tags
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TagsDrawerViewHolder, position: Int) {

    }

    inner class TagsDrawerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkTag = itemView.findViewById<CheckBox>(R.id.select_tag)!!
        val moreOptions = itemView.findViewById<ImageView>(R.id.more_options)!!
    }
}