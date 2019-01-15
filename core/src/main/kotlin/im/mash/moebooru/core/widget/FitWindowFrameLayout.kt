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

package im.mash.moebooru.core.widget

import android.content.Context
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout


/**
 * Use for fragment that make [FrameLayout] can be system awared. by their children.
 * @see <https://stackoverflow.com/a/47349880/3979479>
 */
class FitWindowFrameLayout : FrameLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
            attrs,
            defStyleAttr) {
        setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View, child: View) {
                ViewCompat.requestApplyInsets(this@FitWindowFrameLayout)
            }

            override fun onChildViewRemoved(parent: View, child: View) {
                ViewCompat.requestApplyInsets(this@FitWindowFrameLayout)
            }
        })
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            val childCount = childCount
            for (index in 0 until childCount) {
                ViewCompat.dispatchApplyWindowInsets(getChildAt(index), WindowInsetsCompat(insets))
            }
            return@setOnApplyWindowInsetsListener insets
        }
    }

}