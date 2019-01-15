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

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
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