package im.mash.moebooru.core.application.widget

import android.view.View

//https://github.com/OCNYang/PageTransformerHelp
class AccordionTransformer : BaseTransformer() {

    override fun onTransform(page: View, position: Float) {
        page.pivotX = (if (position < 0) 0 else page.width).toFloat()
        page.scaleX = if (position < 0) 1f + position else 1f - position
    }

}