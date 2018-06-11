package im.mash.moebooru.core.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ScrollView

class FitScrollView : ScrollView {

    companion object {
        private const val TAG = "FitScrollView"
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var isScrolledToTop = true
    private var isScrolledToBottom = false

    private var lastY = 0F

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        if (scrollY == 0) {
            isScrolledToTop = clampedY
            isScrolledToBottom = false
        } else {
            isScrolledToTop = false
            isScrolledToBottom = clampedY
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> lastY = ev.y
            MotionEvent.ACTION_MOVE -> {
                if ((ev.y - lastY) < 0 && isScrolledToBottom) {
                    return false
                }
                if ((ev.y - lastY) > 0 && isScrolledToTop) {
                    return false
                }
                if (!canScroll()) {
                    return false
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun canScroll(): Boolean {
        val childView = getChildAt(0)
        return height < childView.height + paddingBottom + paddingTop
    }
}