package im.mash.moebooru.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import im.mash.moebooru.R
import im.mash.moebooru.core.application.SlidingActivity

class DetailActivity : SlidingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initView()
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout)
        val toolbar = layoutInflater.inflate(R.layout.layout_toolbar, null)
        appBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.toolbar_post))
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
        appBarLayout.addView(toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
            val paddingTop = insets.systemWindowInsetTop
            appBarLayout.minimumHeight = toolbar.minimumHeight + paddingTop
            toolbar.setPadding(0, paddingTop, 0, 0)
            appBarLayout.removeView(toolbar)
            appBarLayout.addView(toolbar)
            insets
        }
    }
}