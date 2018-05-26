/*
 * Copyright (C) 2018 by onlymash <im@mash.im>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package im.mash.moebooru.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import im.mash.moebooru.R

class TagsDrawerAdapter(private val context: Context,
                        private var itemsTag: MutableList<String>?) : RecyclerView.Adapter<TagsDrawerAdapter.TagsDrawerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsDrawerViewHolder {
        val itemView: View = LayoutInflater.from(context)
                .inflate(R.layout.layout_drawer_tags_item, parent, false)
        return TagsDrawerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
       return itemsTag?.size?:0
    }

    fun updateData(itemsTag: MutableList<String>) {
        this.itemsTag = itemsTag
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TagsDrawerViewHolder, position: Int) {
        holder.checkTag.text = itemsTag?.get(position)!!
    }

    inner class TagsDrawerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkTag = itemView.findViewById<CheckBox>(R.id.select_tag)!!
        val moreOptions = itemView.findViewById<ImageView>(R.id.more_options)!!
    }
}