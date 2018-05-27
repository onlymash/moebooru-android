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

package im.mash.moebooru

import android.app.Application
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import android.webkit.WebView
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import im.mash.moebooru.database.DatabaseBoorusManager
import im.mash.moebooru.database.DatabaseHelper
import im.mash.moebooru.database.DatabasePostsManager
import im.mash.moebooru.utils.DeviceContext
import io.fabric.sdk.android.Fabric

class App : Application() {

    companion object {
        lateinit var app: App
        private const val TAG = "MoebooruApp"
    }

    private val deviceContext: Context by lazy { if (Build.VERSION.SDK_INT < 24) this else DeviceContext(this) }
    internal val settings: Settings by lazy { Settings(this) }
    private val database: DatabaseHelper by lazy { DatabaseHelper.getInstance(this) }
    internal val boorusManager: DatabaseBoorusManager by lazy { DatabaseBoorusManager.getInstance(database) }
    internal val postsManager: DatabasePostsManager by lazy { DatabasePostsManager.getInstance(database) }

    override fun onCreate() {
        super.onCreate()
        app = this
        //init night mode
        AppCompatDelegate.setDefaultNightMode(settings.nightMode)
        //init firebase
        FirebaseApp.initializeApp(deviceContext)
        //init Crashlytics
        if (settings.enabledCrashReport) {
            Log.i(TAG, "CrashReport enabled!!")
            Fabric.with(this, Crashlytics())
        }
        //init User-Agent
        try {
            val ua = WebView(this).settings.userAgentString
            settings.userAgentWebView = ua
        } catch (e: Exception) {
            Log.i(TAG, "Get WebView User-Agent failed!!")
        }
    }
}