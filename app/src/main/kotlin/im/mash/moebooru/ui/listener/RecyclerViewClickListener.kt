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

package im.mash.moebooru.ui.listener

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View

open class RecyclerViewClickListener(context: Context, private val listener: OnItemClickListener) : RecyclerView.OnItemTouchListener {

    //手势检测类
    private val gestureDetector: GestureDetector

    private var childView: View? = null

    //内部接口，定义点击方法以及长按方法
    interface OnItemClickListener {

        fun onItemClick(itemView: View?, position: Int)

        fun onItemLongClick(itemView: View?, position: Int)

    }

    init {
        gestureDetector = GestureDetector(context,
                object : GestureDetector.SimpleOnGestureListener() { //这里选择SimpleOnGestureListener实现类，可以根据需要选择重写的方法
                    //单击事件
                    override fun onSingleTapUp(e: MotionEvent): Boolean {
                        val position: Int? = childView?.tag as Int
                        if (position != null && position > -1) {
                            listener.onItemClick(childView, position)
                            return true
                        }
                        return false
                    }

                    //长按事件
                    override fun onLongPress(e: MotionEvent) {
                        val position: Int? = childView?.tag as Int
                        if (position != null && position > -1) {
                            listener.onItemLongClick(childView, position)
                            //长按振动
                            childView!!.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        }
                    }
                })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        childView = rv.findChildViewUnder(e.x, e.y)
        //把事件交给 GestureDetector 处理
        return gestureDetector.onTouchEvent(e)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
