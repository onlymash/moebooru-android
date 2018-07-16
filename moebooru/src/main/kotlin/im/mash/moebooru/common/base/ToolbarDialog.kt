package im.mash.moebooru.common.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.appcompat.widget.Toolbar
import im.mash.moebooru.R
import im.mash.moebooru.core.application.SlidingDialog

abstract class ToolbarDialog(context: Context, private val contentViewResId: Int) : SlidingDialog(context) {

    internal lateinit var toolbar: Toolbar
    internal lateinit var appBarLayout: AppBarLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentViewResId)
        initView()
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        appBarLayout = findViewById(R.id.appbar_layout)
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