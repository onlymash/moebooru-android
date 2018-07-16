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

package im.mash.moebooru.core.widget

import android.content.Context
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration

/**
 * https://stackoverflow.com/a/27973749
 *
 * SlidingPaneLayout that, if closed, checks if children can scroll before it intercepts
 * touch events.  This allows it to contain horizontally scrollable children without
 * intercepting all of their touches.
 *
 * To handle cases where the user is scrolled very far to the right, but should still be
 * able to open the pane without the need to scroll all the way back to the start, this
 * view also adds edge touch detection, so it will intercept edge swipes to open the pane.
 */
class PagerEnabledSlidingPaneLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
        SlidingPaneLayout(context, attrs, defStyle) {

    private var mInitialMotionX: Float = 0F
    private var mInitialMotionY: Float = 0F
    private val mEdgeSlop: Float

    init {
        val config = ViewConfiguration.get(context)
        mEdgeSlop = config.scaledEdgeSlop.toFloat()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mInitialMotionX = ev.x
                mInitialMotionY = ev.y
            }

            MotionEvent.ACTION_MOVE -> {
                val x = ev.x
                val y = ev.y
                // The user should always be able to "close" the pane, so we only check
                // for child scrollability if the pane is currently closed.
                if (mInitialMotionX > mEdgeSlop && !isOpen && canScroll(this, false,
                                Math.round(x - mInitialMotionX), Math.round(x), Math.round(y))) {

                    // How do we set super.mIsUnableToDrag = true?

                    // send the parent a cancel event
                    val cancelEvent = MotionEvent.obtain(ev)
                    cancelEvent.action = MotionEvent.ACTION_CANCEL
                    return super.onInterceptTouchEvent(cancelEvent)
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }
}