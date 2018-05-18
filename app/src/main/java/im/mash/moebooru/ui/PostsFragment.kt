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

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.view.*
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.widget.ImageView
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.utils.Key

@SuppressLint("RtlHardcoded")
class PostsFragment : ToolbarFragment(), Toolbar.OnMenuItemClickListener, View.OnClickListener {

    private lateinit var drawer: Drawer
    private lateinit var drawerView: View
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToolbar: Toolbar
    private lateinit var close: ImageView

    private var metric: DisplayMetrics = DisplayMetrics()
    private var width: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerView = inflater.inflate(R.layout.layout_drawer_posts, container, false)
        return inflater.inflate(R.layout.layout_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setTitle(R.string.posts)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener(this)
        setGridItem()

        val activity = activity!!
        activity.windowManager.defaultDisplay.getMetrics(metric)
        width = metric.widthPixels

        drawer = DrawerBuilder()
                .withActivity(activity)
                .withDrawerGravity(Gravity.RIGHT)
                .withDisplayBelowStatusBar(true)
                .withRootView(R.id.fragment_main)
                .withCustomView(drawerView)
                .withSavedInstance(savedInstanceState)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerWidthPx((width*0.75F).toInt())
                .buildForFragment()
        drawerLayout = drawer.drawerLayout

        drawerToolbar = drawerLayout.findViewById(R.id.toolbar_drawer_posts)
        close = drawerLayout.findViewById<ImageView>(R.id.close)
        close.setOnClickListener(this)
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

    override fun onResume() {
        super.onResume()
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT)
    }

    override fun onPause() {
        super.onPause()
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT)
    }

    override fun onDestroy() {
        super.onDestroy()
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT)
    }

    override fun onClick(v: View?) {
        when (v) {
            close -> drawer.closeDrawer()
        }
    }
}