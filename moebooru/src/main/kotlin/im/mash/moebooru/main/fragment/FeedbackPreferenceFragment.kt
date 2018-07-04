package im.mash.moebooru.main.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
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

class FeedbackPreferenceFragment  : PreferenceFragment() {

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
            val logs = CrashFile().getLogDir(this.requireContext())
            val list = logs.list()
            if (list == null || list.isEmpty()) {
                takeSnackbarShort(view, "Not crash log", paddingButton)
                return@setOnPreferenceClickListener true
            }
            Arrays.sort(list)
            AlertDialog.Builder(this.requireContext())
                    .setItems(list) { _, which ->
                        MailUtil.mailFile(this@FeedbackPreferenceFragment.requireContext(),
                                "feedback@fiepi.me", UriRetriever.getUriFromFile(this.requireContext(), File(logs, list[which])))
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