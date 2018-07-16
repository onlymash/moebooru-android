package im.mash.moebooru.common.base

import android.content.Context
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import android.util.AttributeSet
import im.mash.moebooru.util.screenWidth

class FullScreenNavigationView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NavigationView(context, attrs, defStyleAttr) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        val viewTreeObserver = this.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener {
            setFullScreenWidth(context)
        }
    }

    private fun setFullScreenWidth(context: Context) {
        val lp = this.layoutParams as DrawerLayout.LayoutParams
        lp.width = context.screenWidth
        this.layoutParams = lp
    }
}