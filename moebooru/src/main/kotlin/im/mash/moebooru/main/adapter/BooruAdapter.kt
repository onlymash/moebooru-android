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
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import im.mash.moebooru.R
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.util.ColorUtil
import im.mash.moebooru.util.TextUtil
import im.mash.moebooru.util.logi

class BooruAdapter(private val context: Context, private var boorus: MutableList<Booru>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "BooruAdapter"
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_DETAIL = 1
    }

    private var detailPosition = -1
    private var booruChangeListener: BooruChangeListener? = null

    fun updateData(boorus: MutableList<Booru>) {
        this.boorus = boorus
        detailPosition = -1
        logi(TAG, "boorus size: ${boorus.size}")
        notifyDataSetChanged()
    }

    fun setBooruChangeListener(listener: BooruChangeListener) {
        booruChangeListener = listener
    }

    interface BooruChangeListener {
        fun onBooruEdit(booru: Booru)
        fun onBooruDelete(booru: Booru)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_booru_item, parent, false)
                BooruViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_booru_item_detail, parent, false)
                BooruDetailViewHolder(itemView)
            }
        }
    }

    override fun getItemCount(): Int {
        if (detailPosition >= 0) {
            return boorus.size + 1
        }
        return boorus.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        when (detailPosition >= 0) {
            true -> {
                when {
                    position <= detailPosition -> {
                        val booru = boorus[position]
                        holder as BooruViewHolder
                        val text = booru.name[0].toString()
                        holder.icon.setImageDrawable(TextUtil.textDrawableBuilder().buildRound(text, ColorUtil.getCustomizedColor(context, text)))
                        holder.name.text = booru.name
                        holder.itemView.setOnClickListener {
                            detailPosition = if (position == detailPosition) {
                                -1
                            } else {
                                position
                            }
                            notifyDataSetChanged()
                        }
                    }
                    position == detailPosition + 1 -> {
                        val booru = boorus[detailPosition]
                        holder as BooruDetailViewHolder
                        holder.schema.text = booru.scheme
                        holder.domain.text = booru.host
                        holder.hashSalt.text = booru.hash_salt
                        holder.edit.setOnClickListener {
                            detailPosition = -1
                            booruChangeListener?.onBooruEdit(booru)
                        }
                        holder.delete.setOnClickListener {
                            detailPosition = -1
                            booruChangeListener?.onBooruDelete(booru)
                        }
                    }
                    else -> {
                        val booru =  boorus[position -1]
                        holder as BooruViewHolder
                        val text = booru.name[0].toString()
                        holder.icon.setImageDrawable(TextUtil.textDrawableBuilder().buildRound(text, ColorUtil.getCustomizedColor(context, text)))
                        holder.name.text = booru.name
                        holder.itemView.setOnClickListener {
                            detailPosition = position-1
                            notifyDataSetChanged()
                        }
                    }
                }
            }
            else -> {
                val booru = boorus[position]
                holder as BooruViewHolder
                val text = booru.name[0].toString()
                holder.icon.setImageDrawable(TextUtil.textDrawableBuilder().buildRound(text, ColorUtil.getCustomizedColor(context, text)))
                holder.name.text = booru.name
                holder.itemView.setOnClickListener {
                    detailPosition = position
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (detailPosition >= 0 && position == detailPosition + 1) {
            return VIEW_TYPE_DETAIL
        }
        return VIEW_TYPE_NORMAL
    }

    inner class BooruViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val name: TextView = itemView.findViewById(R.id.name)
        val expand: ImageView = itemView.findViewById(R.id.expand)
    }

    inner class BooruDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val schema: TextView = itemView.findViewById(R.id.schema)
        val domain: TextView = itemView.findViewById(R.id.domain)
        val hashSalt: TextView = itemView.findViewById(R.id.hash_salt)
        val edit: Button = itemView.findViewById(R.id.btn_edit)
        val delete: Button = itemView.findViewById(R.id.btn_delete)
    }

}