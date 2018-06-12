package im.mash.moebooru.common.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.Toolbar
import im.mash.moebooru.R
import im.mash.moebooru.core.application.SlidingDialog

abstract class ToolbarDialog(context: Context, private val contentViewResId: Int) : SlidingDialog(context) {

    internal lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentViewResId)
        initView()
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout)
        appBarLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.toolbar_post))
        toolbar = layoutInflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent))
        appBarLayout.addView(toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
            appBarLayout.minimumHeight = insets.systemWindowInsetTop + toolbar.minimumHeight
            toolbar.setPadding(0, insets.systemWindowInsetTop, 0, 0)
            insets
        }
        toolbar.setNavigationOnClickListener { dismiss() }
    }
}