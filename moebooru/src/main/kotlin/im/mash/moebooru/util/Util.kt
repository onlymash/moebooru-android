package im.mash.moebooru.util

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders

val Context.screenWidth: Int
    get() {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metric: DisplayMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metric)
        return metric.widthPixels
    }

val Context.screenHeight: Int
    get() {
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metric: DisplayMetrics = DisplayMetrics()
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