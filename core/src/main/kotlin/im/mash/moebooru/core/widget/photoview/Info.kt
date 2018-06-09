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