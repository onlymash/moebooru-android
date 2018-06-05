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

package im.mash.moebooru.ui.adapter.deatils

import android.view.View

import im.mash.moebooru.R

class TypeFactoryImpl : TypeFactory {

    override fun type(postInfo: PostInfo): Int = R.layout.layout_details_info
    override fun type(tagInfo: TagInfo): Int = R.layout.layout_details_tags
    override fun type(pagerInfo: PagerInfo): Int = R.layout.layout_details_pager

    override fun createViewHolder(type: Int, itemView: View): BaseViewHolder<*>? {
        return when (type) {
            R.layout.layout_details_tags -> DetailsTagsViewHolder(itemView)
            R.layout.layout_details_info -> DetailsInfoViewHolder(itemView)
            R.layout.layout_details_pager -> DetailsPostViewHolder(itemView)
            else -> null
        }
    }
}
