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
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import im.mash.moebooru.database.DatabaseBoorusManager
import im.mash.moebooru.database.DatabaseHelper
import im.mash.moebooru.models.Booru
import im.mash.moebooru.utils.DeviceContext

class App : Application() {

    companion object {
        lateinit var app: App
    }

    val settings: Settings by lazy {
        Settings(this)
    }

    val database: DatabaseHelper by lazy {
        DatabaseHelper.getInstance(this)
    }

    val boorusManager: DatabaseBoorusManager by lazy {
        DatabaseBoorusManager.getInstance(database)
    }

    val deviceContext: Context by lazy { if (Build.VERSION.SDK_INT < 24) this else DeviceContext(this) }
    val handler: Handler by lazy { Handler(Looper.getMainLooper()) }
    val activeBooru: Booru by lazy { boorusManager.getBooru(settings.activeProfile) }

    override fun onCreate() {
        super.onCreate()
        app = this
        AppCompatDelegate.setDefaultNightMode(settings.nightMode)
        FirebaseApp.initializeApp(deviceContext)
    }

}