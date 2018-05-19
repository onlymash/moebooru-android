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

    private lateinit var sContext:Context
    private lateinit var sSharedPreferences: SharedPreferences
    private lateinit var sEditor: SharedPreferences.Editor

    fun initialize(context: Context) {
        sContext = context.applicationContext
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext)
    }

    var nightModeString: String
        get() = sSharedPreferences.getString(Key.NIGHT_MODE, Key.NIGHT_MODE_SYSTEM)
        set(value) {
            sEditor = sSharedPreferences.edit()
            sEditor.putString(Key.NIGHT_MODE, value)
            sEditor.apply()
        }

    @AppCompatDelegate.NightMode
    val nightMode: Int get() = when (nightModeString) {
        Key.NIGHT_MODE_AUTO -> AppCompatDelegate.MODE_NIGHT_AUTO
        Key.NIGHT_MODE_OFF -> AppCompatDelegate.MODE_NIGHT_NO
        Key.NIGHT_MODE_ON -> AppCompatDelegate.MODE_NIGHT_YES
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    var gridModeString: String
        get() = sSharedPreferences.getString(Key.GRID_MODE, Key.GRID_MODE_STAGGERED_GRID)
        set(value) {
            sEditor = sSharedPreferences.edit()
            sEditor.putString(Key.GRID_MODE, value)
            sEditor.apply()
        }

    var postLimitInt: Int
        get() = sSharedPreferences.getString(Key.POST_LIMIT, "30").toInt()
        set(value) {
            sEditor = sSharedPreferences.edit()
            sEditor.putString(Key.POST_LIMIT, value.toString())
            sEditor.apply()
        }

    var cacheMemoryInt: Int
        get() = sSharedPreferences.getString(Key.CACHE_MEMORY, "256").toInt()
        set(value) {
            sEditor = sSharedPreferences.edit()
            sEditor.putString(Key.CACHE_MEMORY, value.toString())
            sEditor.apply()
        }

    var cacheDiskInt: Int
        get() = sSharedPreferences.getString(Key.CACHE_DISK, "256").toInt()
        set(value) {
            sEditor = sSharedPreferences.edit()
            sEditor.putString(Key.CACHE_DISK, value.toString())
            sEditor.apply()
        }

    var activeProfile: Long
        get() = sSharedPreferences.getLong(Key.ACTIVE_PROFILE, 0L)
        set(value) {
            sEditor = sSharedPreferences.edit()
            sEditor.putLong(Key.ACTIVE_PROFILE, value)
            sEditor.apply()
        }
}