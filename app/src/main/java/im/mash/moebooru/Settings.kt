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

import android.annotation.SuppressLint
import android.content.Context;
import android.content.SharedPreferences
import android.support.v7.app.AppCompatDelegate
import im.mash.moebooru.utils.Key
import moe.shizuku.preference.PreferenceManager

@SuppressLint("StaticFieldLeak")
object Settings {

    internal lateinit var sContext:Context
    internal lateinit var sSharedPreferences: SharedPreferences
    internal lateinit var sEditor: SharedPreferences.Editor

    @SuppressLint("CommitPrefEdits")
    fun initialize(context: Context) {
        sContext = context.applicationContext
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext)
        sEditor = sSharedPreferences.edit()
    }

    var nightModeString: String
        get() = sSharedPreferences.getString(Key.night_mode, Key.night_mode_system)
        set(value) = sEditor.putString(Key.night_mode, value).apply()

    @AppCompatDelegate.NightMode
    val nightMode: Int get() = when (nightModeString) {
        Key.night_mode_auto -> AppCompatDelegate.MODE_NIGHT_AUTO
        Key.night_mode_off -> AppCompatDelegate.MODE_NIGHT_NO
        Key.night_mode_on -> AppCompatDelegate.MODE_NIGHT_YES
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

}