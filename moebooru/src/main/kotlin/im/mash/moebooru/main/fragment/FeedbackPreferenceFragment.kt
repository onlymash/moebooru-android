/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package im.mash.moebooru.main.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import android.view.View
import android.widget.FrameLayout
import im.mash.moebooru.R
import im.mash.moebooru.content.UriRetriever
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.util.crash.CrashFile
import im.mash.moebooru.util.MailUtil
import im.mash.moebooru.util.takeSnackbarShort
import moe.shizuku.preference.PreferenceFragment
import java.io.File
import java.util.*

class FeedbackPreferenceFragment : PreferenceFragment() {

    companion object {
        private const val TAG = "FeedbackPreferenceFragment"
    }

    private var paddingButton = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            paddingButton = insets.systemWindowInsetBottom
            val lp = view.layoutParams as FrameLayout.LayoutParams
            lp.setMargins(0, 0, 0, paddingButton)
            view.layoutParams = lp
            insets
        }
        view.setBackgroundColor(ContextCompat.getColor(activity as MainActivity, R.color.background))
        val sendLog = preferenceScreen.findPreference("send_crash_log")
        sendLog.setOnPreferenceClickListener {
            val logs = CrashFile().getLogDir(requireContext())
            val list = logs.list()
            if (list == null || list.isEmpty()) {
                takeSnackbarShort(view, "Not crash log", paddingButton)
                return@setOnPreferenceClickListener true
            }
            Arrays.sort(list)
            AlertDialog.Builder(activity)
                    .setItems(list) { _, which ->
                        MailUtil.mailFile(this@FeedbackPreferenceFragment.requireContext(),
                                "feedback@fiepi.me", UriRetriever.getUriFromFile(requireContext(), File(logs, list[which])))
//                        logi(TAG, File(logs, list[which]).absolutePath)
                    }
                    .show()
            return@setOnPreferenceClickListener true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_feedback, null)
    }
}