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