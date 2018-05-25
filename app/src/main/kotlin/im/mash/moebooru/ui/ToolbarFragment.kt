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
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.Toolbar
import android.view.View
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R

open class ToolbarFragment : Fragment() {

    protected lateinit var toolbar: Toolbar
    private lateinit var toolbarLayout: CollapsingToolbarLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)
        toolbar.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.toolbar))
        view.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.background))
        toolbarLayout = view.findViewById(R.id.toolbar_layout)
        val activity = activity
        if (activity is MainActivity){
            activity.drawer.setToolbar(activity, toolbar, true)
            ViewCompat.setOnApplyWindowInsetsListener(toolbarLayout) { _, insets ->
                val statusBarSize = insets.systemWindowInsetTop
                toolbarLayout.setPadding(0, statusBarSize, 0, 0)
                if (this is PostsFragment) {
                    activity.drawer.stickyFooter.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
                    app.settings.statusBarHeightInt = statusBarSize
                }
                insets
            }
        } else {
            ViewCompat.setOnApplyWindowInsetsListener(toolbarLayout) { _, insets ->
                toolbarLayout.setPadding(0, insets.systemWindowInsetTop, 0, 0)
                insets
            }
        }
    }

    open fun onBackPressed(): Boolean = false
}