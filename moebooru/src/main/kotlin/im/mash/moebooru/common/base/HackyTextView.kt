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

package im.mash.moebooru.common.base

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.view.isGone
import androidx.appcompat.widget.AppCompatTextView
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class HackyTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        AppCompatTextView(context, attrs, defStyleAttr) {
    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        isGone = text.isNullOrEmpty()
    }
    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) = try {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    } catch (e: IndexOutOfBoundsException) {
        e.printStackTrace()
        if (Fabric.isInitialized()) {
            Crashlytics.logException(e)
        } else {}

    }
    override fun onTouchEvent(event: MotionEvent?) = try {
        super.onTouchEvent(event)
    } catch (e: IndexOutOfBoundsException) {
        e.printStackTrace()
        if (Fabric.isInitialized()) Crashlytics.logException(e)
        false
    }
}