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