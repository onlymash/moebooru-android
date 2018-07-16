package im.mash.moebooru.util

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import androidx.browser.customtabs.CustomTabsIntent
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import im.mash.moebooru.R
import java.util.*

val Context.screenWidth: Int
    get() {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metric = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metric)
        return metric.widthPixels
    }

val Context.screenHeight: Int
    get() {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metric = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metric)
        return metric.heightPixels
    }

val Context.toolbarHeight: Int
    get() {
        val tv = TypedValue()
        var height = 0
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            height = TypedValue.complexToDimensionPixelOffset(tv.data, resources.displayMetrics)
        }
        return height
    }

private val fieldChildFragmentManager by lazy {
    val field = Fragment::class.java.getDeclaredField("mChildFragmentManager")
    field.isAccessible = true
    field
}

var Fragment.childFragManager: FragmentManager?
    get() = childFragmentManager
    set(value) = fieldChildFragmentManager.set(this, value)

val Context.userAgent: String
    get() = android.webkit.WebSettings.getDefaultUserAgent(this)


val Context.glideHeader: Headers
    get() = LazyHeaders.Builder().addHeader(Key.USER_AGENT_KEY, userAgent).build()

val Context.okHttpHeader: List<Pair<String, String>>
    get() = listOf(Pair(Key.USER_AGENT_KEY, userAgent))

val Context.okDownloadHeaders: Map<String, List<String>>
    get() = mapOf(Key.USER_AGENT_KEY to listOf(userAgent))

fun mayRequestStoragePermission(activity: Activity, requestCode: Int): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }
    if (activity.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        return true
    }
    activity.requestPermissions(arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), requestCode)
    return false
}

val statusBarHeight: Int
    get() {
        val res = Resources.getSystem()
        val resId = res.getIdentifier("status_bar_height", "dimen", "android")
        return if (resId > 0) res.getDimensionPixelSize(resId) else 0
    }

val navBarHeight: Int
    get() {
        val res = Resources.getSystem()
        val resId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resId > 0) res.getDimensionPixelSize(resId) else 0
    }

fun formatDate(time: Long): CharSequence {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.timeInMillis = time
    return DateFormat.format("yyyy-MM-dd HH:mm", cal)
}

private fun getCustomTabsIntent(context: Context): CustomTabsIntent {
    return CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.primary))
            .build()
}

fun Context.launchUrl(uri: Uri) = try {
    getCustomTabsIntent(this).launchUrl(this, uri)
} catch (_: ActivityNotFoundException) { }  // ignore

fun Context.launchUrl(url: String) = this.launchUrl(Uri.parse(url))

fun copyText(context: Context, label: String, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val cd = ClipData.newPlainText(label, text)
    cm.primaryClip = cd
}

fun takeSnackbarShort(view: View, text: String, paddingButton: Int) {
    val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT)
    snackbar.view.setPadding(0, 0, 0, paddingButton)
    snackbar.show()
}