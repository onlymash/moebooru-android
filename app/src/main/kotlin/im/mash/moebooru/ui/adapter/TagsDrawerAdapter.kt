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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.model.Tag
import im.mash.moebooru.ui.PostsFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class TagsDrawerAdapter(private val postsFragment: PostsFragment,
                        private var itemsTag: MutableList<Tag>?) : RecyclerView.Adapter<TagsDrawerAdapter.TagsDrawerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsDrawerViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_drawer_tags_item, parent, false)
        return TagsDrawerViewHolder(itemView)
    }

    override fun getItemCount(): Int {
       return itemsTag?.size?:0
    }

    fun updateData(itemsTag: MutableList<Tag>) {
        this.itemsTag = itemsTag
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: TagsDrawerViewHolder, position: Int) {
        if (itemCount > 0) {
            holder.checkTag.text = itemsTag!![position].name
            if (itemsTag!![position].is_selected) {
                holder.checkTag.isChecked = true
            }
            holder.itemView.setOnClickListener {
                if (holder.checkTag.isChecked) {
                    holder.checkTag.isChecked = false
                    postsFragment.changeTagStatus(position, false)
                    doAsync {
                        app.tagsManager.updateTag(app.settings.activeProfile, itemsTag!![position].name, false)
                    }
                } else {
                    holder.checkTag.isChecked = true
                    postsFragment.changeTagStatus(position, true)
                    doAsync {
                        app.tagsManager.updateTag(app.settings.activeProfile, itemsTag!![position].name, true)
                    }
                }
            }
            holder.moreOptions.setOnClickListener {
                var popupMenu: PopupMenu? = null
                popupMenu = PopupMenu(postsFragment.context, holder.moreOptions)
                popupMenu.inflate(R.menu.menu_tag_option)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

                    when (item!!.itemId) {
                        R.id.action_remove -> {
                            doAsync {
                                app.tagsManager.deleteTag(app.settings.activeProfile, itemsTag!![position].name)
                                uiThread {
                                    postsFragment.deleteTag(position)
                                }
                            }
                        }
                        R.id.action_copy -> {
                            postsFragment.copyTag(position)
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        }
    }

    inner class TagsDrawerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkTag = itemView.findViewById<CheckBox>(R.id.select_tag)!!
        val moreOptions = itemView.findViewById<ImageView>(R.id.more_options)!!
    }
}