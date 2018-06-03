package im.mash.moebooru.ui.widget.photoview

import android.view.MotionEvent

class RotateGestureDetector(private val mListener: OnRotateListener) {

    companion object {
        private val MAX_DEGREES_STEP = 120
    }

    private var mPrevSlope: Float = 0F
    private var mCurrSlope: Float = 0F

    private var x1: Float = 0F
    private var y1: Float = 0F
    private var x2: Float = 0F
    private var y2: Float = 0F

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
            else -> {
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

interface OnRotateListener {
    fun onRotate(degrees: Float, focusX: Float, focusY: Float)
}