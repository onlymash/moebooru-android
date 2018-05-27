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
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.Toolbar
import android.view.View
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R

open class ToolbarFragment : Fragment() {

    protected lateinit var appBarLayout: AppBarLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.background))
        appBarLayout = view.findViewById(R.id.appbar_layout)
    }

    open fun onBackPressed(): Boolean = false

    open fun setInsetsListener(toolbar: Toolbar) {
        val activity = activity
        appBarLayout.addView(toolbar)
        if (activity is MainActivity){
            activity.drawer.setToolbar(activity, toolbar, true)
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
                val statusBarSize = insets.systemWindowInsetTop
                appBarLayout.minimumHeight = statusBarSize + activity.toolbarHeight
                toolbar.setPadding(0, statusBarSize, 0, 0)
                appBarLayout.removeView(toolbar)
                appBarLayout.addView(toolbar)
                if (this is PostsFragment) {
                    app.settings.statusBarHeightInt = statusBarSize
                }
                insets
            }
        } else if (activity is DetailsActivity){
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
                val statusBarSize = insets.systemWindowInsetTop
                appBarLayout.minimumHeight = statusBarSize + activity.toolbarHeight
                toolbar.setPadding(0, statusBarSize, 0, 0)
                appBarLayout.removeView(toolbar)
                appBarLayout.addView(toolbar)
                insets
            }
        }
    }
}