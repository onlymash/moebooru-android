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
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.Headers
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.glide.GlideApp
import im.mash.moebooru.model.RawPost
import im.mash.moebooru.ui.widget.FixedImageView
import im.mash.moebooru.utils.Key
import im.mash.moebooru.utils.glideHeader

class PostsAdapter(private val context: Context, private val itemPadding: Int, private val itemPaddingTop: Int,
                   private var items: MutableList<RawPost>?) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    companion object {
        private val TAG = this::class.java.simpleName
        private val header: Headers = glideHeader
        private var tagItems: MutableList<MutableList<String>> = mutableListOf()
    }

    fun updateData(items: MutableList<RawPost>?) {
        this.items = items
        notifyDataSetChanged()
    }

    fun addData(items: MutableList<RawPost>?) {
        val countBefore = itemCount
        this.items = items
        notifyItemRangeInserted(countBefore, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size?:0
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        //给 itemView 加上位置 tag， 以便监听点击事件时获取位置
        holder.itemView.tag = position
        if (itemCount > 0) {
            if (position in 0..(app.settings.spanCountInt - 1)) {
                holder.itemView.setPadding(itemPadding, itemPaddingTop, itemPadding, itemPadding)
            } else {
                holder.itemView.setPadding(itemPadding, itemPadding, itemPadding, itemPadding)
            }
            val placeHolderId = when (items!![position].rating) {
                "q" -> R.drawable.background_rating_q
                "e" -> R.drawable.background_rating_e
                else -> R.drawable.background_rating_s
            }
            when (app.settings.gridModeString) {
                Key.GRID_MODE_STAGGERED_GRID -> {
                    holder.fixedImageView.setWidthAndHeightWeight(items!![position].width!!.toInt(), items!![position].height!!.toInt())
                    GlideApp.with(context)
                            .load(GlideUrl(items!![position].preview_url, header))
                            .fitCenter()
                            .placeholder(placeHolderId)
                            .into(holder.fixedImageView)
                }
                else -> {
                    holder.fixedImageView.setWidthAndHeightWeight(1,1)
                    GlideApp.with(context)
                            .load(GlideUrl(items!![position].preview_url, header))
                            .centerCrop()
                            .placeholder(placeHolderId)
                            .into(holder.fixedImageView)
                }
            }
        }
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fixedImageView: FixedImageView = itemView.findViewById(R.id.post_item)
    }
}