package im.mash.moebooru.main.fragment

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.View
import im.mash.moebooru.R
import im.mash.moebooru.main.MainActivity
import moe.shizuku.preference.PreferenceFragment

class FeedbackPreferenceFragment  : PreferenceFragment(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            view.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
            insets
        }
        view.setBackgroundColor(ContextCompat.getColor(activity as MainActivity, R.color.background))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_feedback, null)
    }
}