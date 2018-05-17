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

package im.mash.moebooru.ui

import android.os.Bundle
import android.view.*
import android.support.v7.widget.Toolbar
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.utils.Key

class PostsFragment : ToolbarFragment(), Toolbar.OnMenuItemClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.layout_posts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setTitle(R.string.posts)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener(this)
        setGridItem()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_grid -> {
                Settings.gridModeString = Key.grid_mode_grid
                setGridItem()
            }
            R.id.action_staggered_grid -> {
                Settings.gridModeString = Key.grid_mode_staggered_grid
                setGridItem()
            }
        }
        return true
    }

    fun setGridItem() {
        when (Settings.gridModeString) {
            Key.grid_mode_grid -> toolbar.menu.findItem(R.id.action_grid).setChecked(true)
            Key.grid_mode_staggered_grid -> toolbar.menu.findItem(R.id.action_staggered_grid).setChecked(true)
        }
    }
}