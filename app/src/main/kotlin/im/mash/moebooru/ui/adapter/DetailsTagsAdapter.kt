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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.model.Tag
import im.mash.moebooru.ui.DetailsActivity
import im.mash.moebooru.ui.DetailsFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class DetailsTagsAdapter internal constructor(private val tagsFragment: DetailsFragment.TagsFragment,
                                              private var tags: List<String>?) : RecyclerView.Adapter<DetailsTagsAdapter.DetailsTagsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsTagsViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_details_tags_item, parent, false)
        return DetailsTagsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tags?.size?:0
    }

    fun updateData(tags: List<String>?) {
        this.tags = tags
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: DetailsTagsViewHolder, position: Int) {
        if (itemCount > 0 ) {
            holder.itemView.tag = position
            holder.tagName.text = tags!![position]
            holder.itemView.setOnClickListener {
                tagsFragment.clickTag(position)
            }
            holder.itemView.setOnLongClickListener {
                val cm: ClipboardManager = tagsFragment.requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as  ClipboardManager
                val cd = ClipData.newPlainText("Tag: $position", tags!![position])
                cm.primaryClip = cd
                val snackbar = Snackbar.make(it.rootView, tags!![position] + " has been copied", Snackbar.LENGTH_SHORT)
                snackbar.view.setPadding(0, 0, 0, (tagsFragment.activity as DetailsActivity).bottomHeight)
                snackbar.show()
                true
            }
            holder.tagAdd.setOnClickListener {
                doAsync {
                    app.tagsManager.saveTag(Tag(app.settings.activeProfile, tags!![position], false))
                    uiThread {
                        val snackbar = Snackbar.make(holder.itemView.rootView, tags!![position] + " has been added to search list", Snackbar.LENGTH_SHORT)
                        snackbar.view.setPadding(0, 0, 0, (tagsFragment.activity as DetailsActivity).bottomHeight)
                        snackbar.show()
                    }
                }
            }
            holder.tagAddNegated.setOnClickListener {
                doAsync {
                    app.tagsManager.saveTag(Tag(app.settings.activeProfile, "-" + tags!![position], false))
                    uiThread {
                        val snackbar = Snackbar.make(holder.itemView.rootView, "-" + tags!![position] + " has been added to search list", Snackbar.LENGTH_SHORT)
                        snackbar.view.setPadding(0, 0, 0, (tagsFragment.activity as DetailsActivity).bottomHeight)
                        snackbar.show()
                    }
                }
            }
        }
    }

    inner class DetailsTagsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tagName = itemView.findViewById<TextView>(R.id.tag_name)
        val tagAddNegated = itemView.findViewById<ImageButton>(R.id.tag_add_negated)
        val tagAdd = itemView.findViewById<ImageButton>(R.id.tag_add)
    }

}