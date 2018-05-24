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

package im.mash.moebooru.ui.widget

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView

import com.mikepenz.materialdrawer.model.AbstractDrawerItem
import im.mash.moebooru.R

class TagsDrawerItem : AbstractDrawerItem<TagsDrawerItem, TagsDrawerItem.TagsViewHolder>() {

    private var tag = ""

    fun withTag(tag: String) {
        this.tag = tag
    }

    override fun getType(): Int {
        return R.id.drawer_tags_item
    }

    @LayoutRes
    override fun getLayoutRes(): Int {
        return R.layout.layout_drawer_tags_item
    }

    override fun bindView(holder: TagsViewHolder, payloads: List<*>) {
        super.bindView(holder, payloads)
        holder.selectTag.text = tag
        //call the onPostBindView method to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, holder.itemView)
    }

    override fun getViewHolder(view: View): TagsViewHolder {
        return TagsViewHolder(view)
    }

    class TagsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val selectTag = itemView.findViewById<CheckBox>(R.id.select_tag)!!
        val moreOptions = itemView.findViewById<ImageView>(R.id.more_options)!!
    }
}