package im.mash.moebooru.core.application.widget

import android.support.v4.view.ViewPager
import android.view.View


//https://github.com/OCNYang/PageTransformerHelp
abstract class BaseTransformer : ViewPager.PageTransformer {

    /**
     * Indicates if the default animations of the view pager should be used.
     *
     * @return
     */
    open fun isPagingEnabled(): Boolean = false

    /**
     * Called each [.transformPage].
     *
     * @param page
     * Apply the transformation to this page
     * @param position
     * Position of page relative to the current front-and-center position of the pager. 0 is front and
     * center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    protected abstract fun onTransform(page: View, position: Float)

    /**
     * Apply a property transformation to the given page. For most use cases, this method should not be overridden.
     * Instead use [.transformPage] to perform typical transformations.
     *
     * @param page
     * Apply the transformation to this page
     * @param position
     * Position of page relative to the current front-and-center position of the pager. 0 is front and
     * center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    override fun transformPage(page: View, position: Float) {
        onPreTransform(page, position)
        onTransform(page, position)
        onPostTransform(page, position)
    }

    /**
     * If the position offset of a fragment is less than negative one or greater than one, returning true will set the
     * fragment alpha to 0f. Otherwise fragment alpha is always defaulted to 1f.
     *
     * @return
     */
    protected fun hideOffscreenPages(): Boolean {
        return true
    }

    /**
     * Called each [.transformPage] before {[.onTransform].
     *
     *
     * The default implementation attempts to reset all view properties. This is useful when toggling transforms that do
     * not modify the same page properties. For instance changing from a transformation that applies rotation to a
     * transformation that fades can inadvertently leave a fragment stuck with a rotation or with some degree of applied
     * alpha.
     *
     * @param page
     * Apply the transformation to this page
     * @param position
     * Position of page relative to the current front-and-center position of the pager. 0 is front and
     * center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    protected fun onPreTransform(page: View, position: Float) {
        val width = page.width.toFloat()

        page.rotationX = 0f
        page.rotationY = 0f
        page.rotation = 0f
        page.scaleX = 1f
        page.scaleY = 1f
        page.pivotX = 0f
        page.pivotY = 0f
        page.translationY = 0f
        page.translationX = if (isPagingEnabled()) 0f else -width * position

        if (hideOffscreenPages()) {
            page.alpha = if (position <= -1f || position >= 1f) 0f else 1f
            //			page.setEnabled(false);
        } else {
            //			page.setEnabled(true);
            page.alpha = 1f
        }
    }

    /**
     * Called each [.transformPage] after [.onTransform].
     *
     * @param page
     * Apply the transformation to this page
     * @param position
     * Position of page relative to the current front-and-center position of the pager. 0 is front and
     * center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    protected fun onPostTransform(page: View, position: Float) {}

    companion object {

        /**
         * Same as [Math.min] without double casting, zero closest to infinity handling, or NaN support.
         *
         * @param val
         * @param min
         * @return
         */
        protected fun min(`val`: Float, min: Float): Float {
            return if (`val` < min) min else `val`
        }
    }

}
