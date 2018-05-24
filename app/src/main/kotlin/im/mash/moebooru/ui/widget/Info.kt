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

import android.graphics.PointF
import android.graphics.RectF
import android.widget.ImageView

class Info(rect: RectF, img: RectF, widget: RectF, base: RectF, screenCenter: PointF,
           var mScale: Float, var mDegrees: Float, var mScaleType: ImageView.ScaleType?) {

    // 内部图片在整个手机界面的位置
    var mRect = RectF()

    // 控件在窗口的位置
    var mImgRect = RectF()

    var mWidgetRect = RectF()

    var mBaseRect = RectF()

    var mScreenCenter = PointF()

    init {
        mRect.set(rect)
        mImgRect.set(img)
        mWidgetRect.set(widget)
        mBaseRect.set(base)
        mScreenCenter.set(screenCenter)
    }
}