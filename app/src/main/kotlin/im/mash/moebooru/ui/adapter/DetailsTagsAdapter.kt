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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import im.mash.moebooru.R

class DetailsTagsAdapter(private var tags: MutableList<String>?) : RecyclerView.Adapter<DetailsTagsAdapter.DetailsTagsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsTagsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_details_tags_item, parent, false)
        return DetailsTagsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tags?.size?:0
    }

    override fun onBindViewHolder(holder: DetailsTagsViewHolder, position: Int) {
        if (itemCount > 0 ) {
            holder.tagName.text = tags!![position]
        }
    }

    inner class DetailsTagsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tagName = itemView.findViewById<TextView>(R.id.tag_name)
        val tagAdd = itemView.findViewById<ImageView>(R.id.tag_add)
    }

}