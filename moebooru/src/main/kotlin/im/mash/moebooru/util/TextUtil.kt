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

package im.mash.moebooru.util

import android.graphics.Typeface
import im.mash.moebooru.core.widget.TextDrawable

object TextUtil {

    private var builder: TextDrawable.IShapeBuilder? = null

    fun textDrawableBuilder(): TextDrawable.Builder {
        if (builder == null) {
            val builder = TextDrawable.builder()
            builder.beginConfig().width(50)
            builder.beginConfig().height(50)
            builder.beginConfig().fontSize(30)
            builder.beginConfig().useFont(Typeface.create("sans", Typeface.NORMAL))
            builder.beginConfig().withBorder(2)
            this.builder = builder
        }
        return builder as TextDrawable.Builder
    }
}