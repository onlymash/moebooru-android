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

package im.mash.moebooru.utils

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.bumptech.glide.load.model.Headers
import com.bumptech.glide.load.model.LazyHeaders
import im.mash.moebooru.App.Companion.app
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment


private val fieldChildFragmentManager by lazy {
    val field = Fragment::class.java.getDeclaredField("mChildFragmentManager")
    field.isAccessible = true
    field
}

var Fragment.childFragManager: FragmentManager?
    get() = childFragmentManager
    set(value) = fieldChildFragmentManager.set(this, value)

val userAgent: String
    get() = System.getProperty("http.agent")

private val userAgentWebView: String
    get() = app.settings.userAgentWebView

val okHttpHeader: List<Pair<String, String>>
    get() = listOf(Pair(Net.USER_AGENT_KEY, userAgentWebView))

val glideHeader: Headers
    get() = LazyHeaders.Builder().addHeader(Net.USER_AGENT_KEY, userAgentWebView).build()

val okDownloadHeaders: Map<String, List<String>>
    get() = mapOf(Net.USER_AGENT_KEY to listOf(userAgentWebView))

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

fun downloadPost(url: String, title: String, booruName: String, fileName: String) {
    val downloadManager = app.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = Uri.parse(url)
    val request = DownloadManager.Request(uri)
    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
    request.setTitle(title)
    request.setVisibleInDownloadsUi(true)
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "/Moebooru/$booruName/$fileName")
    downloadManager.enqueue(request)
}

fun mayRequestStoragePermission(activity: Activity, requestCode: Int): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }
    if (activity.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        return true
    }
    activity.requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE), requestCode)
    return false
}