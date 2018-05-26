/*
 * Copyright (C) 2018 by onlymash <im@mash.im>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package im.mash.moebooru.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.View
import com.crashlytics.android.Crashlytics
import im.mash.moebooru.App.Companion.app
import im.mash.moebooru.R
import im.mash.moebooru.utils.Key
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
            Key.ENABLE_CRASH_REPORT -> {
                if (app.settings.enabledCrashReport && !Fabric.isInitialized()) {
                    Fabric.with(app, Crashlytics())
                }
            }
        }
    }
}
