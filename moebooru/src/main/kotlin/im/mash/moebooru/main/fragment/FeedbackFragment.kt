package im.mash.moebooru.main.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import im.mash.moebooru.R
import im.mash.moebooru.common.base.ToolbarFragment
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.util.childFragManager

class FeedbackFragment : ToolbarFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(ContextCompat.getColor(activity as MainActivity, R.color.window_background))
        toolbar.setTitle(R.string.feedback)
        val fm = childFragmentManager
        fm.beginTransaction().replace(R.id.content, FeedbackPreferenceFragment()).commit()
        fm.executePendingTransactions()
    }

    override fun onDetach() {
        super.onDetach()
        childFragManager = null
    }
}