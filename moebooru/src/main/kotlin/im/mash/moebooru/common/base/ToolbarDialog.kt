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
import android.content.Context
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.appcompat.widget.Toolbar
import im.mash.moebooru.R
import im.mash.moebooru.core.application.SlidingDialog

abstract class ToolbarDialog(context: Context, private val contentViewResId: Int) : SlidingDialog(context) {

    internal lateinit var toolbar: Toolbar
    internal lateinit var appBarLayout: AppBarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentViewResId)
        initView()
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        appBarLayout = findViewById(R.id.appbar_layout)
        appBarLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.toolbar_post))
        toolbar = layoutInflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
        appBarLayout.addView(toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
            appBarLayout.minimumHeight = insets.systemWindowInsetTop + toolbar.minimumHeight
            toolbar.setPadding(0, insets.systemWindowInsetTop, 0, 0)
            insets
        }
        toolbar.setNavigationOnClickListener { dismiss() }
    }
}