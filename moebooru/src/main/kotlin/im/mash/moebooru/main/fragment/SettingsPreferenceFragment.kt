package im.mash.moebooru.main.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.View
import com.crashlytics.android.Crashlytics
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.Settings
import im.mash.moebooru.main.MainActivity
import io.fabric.sdk.android.Fabric
import moe.shizuku.preference.PreferenceFragment

class SettingsPreferenceFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            view.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
            insets
        }
        view.setBackgroundColor(ContextCompat.getColor(activity as MainActivity, R.color.background))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, null)
    }

    override fun onCreateItemDecoration(): DividerDecoration {
        return CategoryDivideDividerDecoration()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Settings.ENABLE_CRASH_REPORT -> {
                if (app.settings.enabledCrashReport && !Fabric.isInitialized()) {
                    Fabric.with(app, Crashlytics())
                }
            }
        }
    }
}