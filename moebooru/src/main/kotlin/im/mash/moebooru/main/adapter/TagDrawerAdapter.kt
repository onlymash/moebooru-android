/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package im.mash.moebooru.main.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import im.mash.moebooru.R
import im.mash.moebooru.common.data.local.entity.Tag

class TagDrawerAdapter(private val context: Context) : RecyclerView.Adapter<TagDrawerAdapter.TagDrawerViewHolder>()  {

    private var tags = mutableListOf<Tag>()
    private var listener: TagItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagDrawerViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_drawer_tags_item, parent, false)
        return TagDrawerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    fun updateData(tags: MutableList<Tag>) {
        this.tags = tags
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TagDrawerViewHolder, position: Int) {
        holder.checkTag.text = tags[position].tag
        holder.checkTag.isChecked = tags[position].is_selected
        holder.moreOptions.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.moreOptions)
            popupMenu.inflate(R.menu.menu_tag_option)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_copy -> {
                        listener?.onCopyTag(position)
                    }
                    R.id.action_remove -> {
                        listener?.onRemoveTag(position)
                    }
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }
        holder.itemView.setOnClickListener {
            holder.checkTag.isChecked = !holder.checkTag.isChecked
            listener?.onClickItemView(position)
        }
    }

    interface TagItemClickListener {
        fun onClickItemView(position: Int)
        fun onCopyTag(position: Int)
        fun onRemoveTag(position: Int)
    }

    fun  setTagItemClickListener(listener: TagItemClickListener) {
        this.listener = listener
    }

    inner class TagDrawerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkTag = itemView.findViewById<CheckBox>(R.id.select_tag)!!
        val moreOptions = itemView.findViewById<ImageView>(R.id.more_options)!!
    }
}