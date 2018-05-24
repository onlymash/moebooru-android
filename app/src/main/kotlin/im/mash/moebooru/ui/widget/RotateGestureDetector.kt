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

package im.mash.moebooru.ui.widget

import android.view.MotionEvent

class RotateGestureDetector(private val mListener: OnRotateListener) {

    private var mPrevSlope: Float = 0F
    private var mCurrSlope: Float = 0F

    private var x1: Float = 0F
    private var y1: Float = 0F
    private var x2: Float = 0F
    private var y2: Float = 0F

    companion object {
        private const val MAX_DEGREES_STEP = 120
    }

    fun onTouchEvent(event: MotionEvent) {

        val action = event.actionMasked

        when (action) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> if (event.pointerCount == 2) mPrevSlope = caculateSlope(event)
            MotionEvent.ACTION_MOVE -> if (event.pointerCount > 1) {
                mCurrSlope = caculateSlope(event)

                val currDegrees = Math.toDegrees(Math.atan(mCurrSlope.toDouble()))
                val prevDegrees = Math.toDegrees(Math.atan(mPrevSlope.toDouble()))

                val deltaSlope = currDegrees - prevDegrees

                if (Math.abs(deltaSlope) <= MAX_DEGREES_STEP) {
                    mListener.onRotate(deltaSlope.toFloat(), (x2 + x1) / 2, (y2 + y1) / 2)
                }
                mPrevSlope = mCurrSlope
            }
        }
    }

    private fun caculateSlope(event: MotionEvent): Float {
        x1 = event.getX(0)
        y1 = event.getY(0)
        x2 = event.getX(1)
        y2 = event.getY(1)
        return (y2 - y1) / (x2 - x1)
    }
}