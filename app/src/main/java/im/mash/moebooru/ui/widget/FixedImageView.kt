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

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

import im.mash.moebooru.R

class FixedImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    private var widthWeight = 1
    private var heightWeight = 1

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.FixedImageView)
        widthWeight = a.getInteger(R.styleable.FixedImageView_widthWeight, 1)
        heightWeight = a.getInteger(R.styleable.FixedImageView_heightWeight, 1)
        a.recycle()
    }

    fun setWidthAndHeightWeight(widthWeight: Int, heightWeight: Int) {
        this.widthWeight = widthWeight
        this.heightWeight = heightWeight
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = this.measuredWidth
        val height = width * heightWeight / widthWeight
        setMeasuredDimension(width + paddingLeft + paddingRight, height + paddingTop + paddingBottom)
    }
}
