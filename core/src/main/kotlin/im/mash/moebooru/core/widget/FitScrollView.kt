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
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ScrollView

class FitScrollView : ScrollView {

    companion object {
        private const val TAG = "FitScrollView"
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var isScrolledToTop = true
    private var isScrolledToBottom = false

    private var lastY = 0F

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        if (scrollY == 0) {
            isScrolledToTop = clampedY
            isScrolledToBottom = false
        } else {
            isScrolledToTop = false
            isScrolledToBottom = clampedY
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.actionMasked) {
            MotionEvent.ACTION_DOWN -> lastY = ev.y
            MotionEvent.ACTION_MOVE -> {
                if (((ev.y - lastY) < 0 && isScrolledToBottom) ||
                        ((ev.y - lastY) > 0 && isScrolledToTop) || !canScroll()) {

                    return false
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun canScroll(): Boolean {
        val childView = getChildAt(0)
        return height < childView.height + paddingBottom + paddingTop
    }
}