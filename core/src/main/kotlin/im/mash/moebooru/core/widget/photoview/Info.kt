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

package im.mash.moebooru.core.widget.photoview

import android.graphics.PointF
import android.graphics.RectF
import android.widget.ImageView

data class Info(
        internal val rect: RectF,
        internal val img: RectF,
        internal val widget: RectF,
        internal val base: RectF,
        internal val screenCenter: PointF,
        internal val scale: Float,
        internal val degrees: Float,
        internal val scaleType: ImageView.ScaleType)