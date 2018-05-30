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
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SlidingPaneLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.R
import im.mash.moebooru.ui.widget.PagerEnabledSlidingPaneLayout

/**
 * https://www.jianshu.com/p/1647bda9305e
 **/

abstract class BaseActivity : AppCompatActivity(), SlidingPaneLayout.PanelSlideListener {

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initSwipeBackFinish()
        super.onCreate(savedInstanceState)
    }

    private fun initSwipeBackFinish() {
        if (isSupportSwipeBack()) {
            val slidingPaneLayout = PagerEnabledSlidingPaneLayout(this)
            //通过反射改变mOverhangSize的值为0，这个mOverhangSize值为菜单到右边屏幕的最短距离，默认
            //是32dp，现在给它改成0
            try {
                //mOverhangSize属性，意思就是左菜单离右边屏幕边缘的距离
                val fOverHang = SlidingPaneLayout::class.java.getDeclaredField("mOverhangSize")
                fOverHang.isAccessible = true
                //设置左菜单离右边屏幕边缘的距离为0，设置全屏
                fOverHang.set(slidingPaneLayout, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            slidingPaneLayout.setPanelSlideListener(this)
            slidingPaneLayout.sliderFadeColor = resources.getColor(android.R.color.transparent)
            // 左侧的透明视图
            val leftView = View(this)
            leftView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            slidingPaneLayout.addView(leftView, 0)  //添加到SlidingPaneLayout中
            // 右侧的内容视图
            val decor = window.decorView as ViewGroup
            val decorChild = decor.getChildAt(0) as ViewGroup
            decorChild.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
            decor.removeView(decorChild)
            decor.addView(slidingPaneLayout)
            // 为 SlidingPaneLayout 添加内容视图
            slidingPaneLayout.addView(decorChild, 1)
        }
    }

    override fun onPanelClosed(panel: View) {

    }

    override fun onPanelSlide(panel: View, slideOffset: Float) {

    }

    override fun onPanelOpened(panel: View) {
        finish()
        this.overridePendingTransition(0, R.anim.out_to_right)
    }

    private fun isSupportSwipeBack(): Boolean {
        return true
    }
}