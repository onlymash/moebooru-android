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