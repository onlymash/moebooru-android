/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package im.mash.moebooru.common.base

import android.annotation.SuppressLint
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.appcompat.widget.Toolbar
import android.view.View
import im.mash.moebooru.R
import im.mash.moebooru.main.MainActivity

abstract class ToolbarFragment: Fragment() {

    protected lateinit var appBarLayout: AppBarLayout
    protected lateinit var toolbar: Toolbar

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.background))
        appBarLayout = view.findViewById(R.id.appbar_layout)
        toolbar = this.requireActivity().layoutInflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        appBarLayout.addView(toolbar)
        setInsetsListener(toolbar)
    }

    fun onBackPressed(): Boolean = false

    private fun setInsetsListener(toolbar: Toolbar) {
        val activity = activity
        when (activity) {
            is MainActivity -> {
                activity.drawer.setToolbar(activity, toolbar, true)
                ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
                    val insetTopPadding = insets.systemWindowInsetTop
                    appBarLayout.minimumHeight = insetTopPadding + toolbar.minimumHeight
                    toolbar.setPadding(0, insetTopPadding, 0, 0)
                    insets
                }
            }
        }
    }


}