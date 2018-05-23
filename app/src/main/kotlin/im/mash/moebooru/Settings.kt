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

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatDelegate
import im.mash.moebooru.utils.Key
import moe.shizuku.preference.PreferenceManager

class Settings(ctx: Context) {

    private val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)

    private val editor: SharedPreferences.Editor = sp.edit()

    var nightModeString: String
        get() = sp.getString(Key.NIGHT_MODE, Key.NIGHT_MODE_SYSTEM)
        set(value) = editor.putString(Key.NIGHT_MODE, value).apply()

    @AppCompatDelegate.NightMode
    val nightMode: Int
        get() = when (nightModeString) {
        Key.NIGHT_MODE_AUTO -> AppCompatDelegate.MODE_NIGHT_AUTO
        Key.NIGHT_MODE_OFF -> AppCompatDelegate.MODE_NIGHT_NO
        Key.NIGHT_MODE_ON -> AppCompatDelegate.MODE_NIGHT_YES
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    var gridModeString: String
        get() = sp.getString(Key.GRID_MODE, Key.GRID_MODE_STAGGERED_GRID)
        set(value) = editor.putString(Key.GRID_MODE, value).apply()

    var postLimitInt: Int
        get() = sp.getString(Key.POST_LIMIT, "30").toInt()
        set(value) = editor.putString(Key.POST_LIMIT, value.toString()).apply()

    var cacheMemoryInt: Int
        get() = sp.getString(Key.CACHE_MEMORY, "256").toInt()
        set(value) = editor.putString(Key.CACHE_MEMORY, value.toString()).apply()

    var cacheDiskInt: Int
        get() = sp.getString(Key.CACHE_DISK, "256").toInt()
        set(value) = editor.putString(Key.CACHE_DISK, value.toString()).apply()

    var activeProfile: Long
        get() = sp.getLong(Key.ACTIVE_PROFILE, 0L)
        set(value) = editor.putLong(Key.ACTIVE_PROFILE, value).apply()

    var spanCountInt: Int
        get() = sp.getInt(Key.SPAN_COUNT, 3)
        set(value) = editor.putInt(Key.SPAN_COUNT, value).apply()

    var isNotMoreData: Boolean
        get() = sp.getBoolean(Key.IS_NOT_MORE_DATA, true)
        set(value) = editor.putBoolean(Key.IS_NOT_MORE_DATA, value).apply()

    var isChangedNightMode: Boolean
        get() = sp.getBoolean(Key.IS_CHANGED_NIGHT_MODE, false)
        set(value) = editor.putBoolean(Key.IS_CHANGED_NIGHT_MODE, value).apply()

}