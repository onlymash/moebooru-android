package im.mash.moebooru.common.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.Toolbar
import android.view.View
import im.mash.moebooru.R
import im.mash.moebooru.main.MainActivity

abstract class ToolbarFragment: Fragment() {

    protected lateinit var appBarLayout: AppBarLayout
    protected lateinit var toolbar: Toolbar

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.background))
        appBarLayout = view.findViewById(R.id.appbar_layout)
        toolbar = this.requireActivity().layoutInflater.inflate(R.layout.layout_toolbar, null) as Toolbar
        appBarLayout.addView(toolbar)
        setInsetsListener(toolbar)
    }

    fun onBackPressed(): Boolean = false

    private fun setInsetsListener(toolbar: Toolbar) {
        val activity = activity
        when (activity) {
            is MainActivity -> {
                activity.drawer.setToolbar(activity, toolbar, true)
                ViewCompat.setOnApplyWindowInsetsListener(appBarLayout) { _, insets ->
                    val insetTopPadding = insets.systemWindowInsetTop
                    appBarLayout.minimumHeight = insetTopPadding + toolbar.minimumHeight
                    toolbar.setPadding(0, insetTopPadding, 0, 0)
                    insets
                }
            }
        }
    }


}