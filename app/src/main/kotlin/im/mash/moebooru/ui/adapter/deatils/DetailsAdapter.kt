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

package im.mash.moebooru.ui.adapter.Deatils

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class DetailsAdapter(private val items: MutableList<Visitable>?) : RecyclerView.Adapter<BaseViewHolder<Visitable>>() {
    private val typeFactory: TypeFactory

    init {
        this.typeFactory = TypeFactoryImpl()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Visitable> {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return typeFactory.createViewHolder(viewType, view) as BaseViewHolder<Visitable>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Visitable>, position: Int) {
        holder.bindViewData(items!![position])
    }

    override fun getItemViewType(position: Int): Int {
        return items!![position].type(typeFactory)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }
}
