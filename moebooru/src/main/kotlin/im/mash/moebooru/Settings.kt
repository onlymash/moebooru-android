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

import android.content.SharedPreferences
import android.support.v7.app.AppCompatDelegate

class Settings(private val sp: SharedPreferences) {

    companion object {
        const val NIGHT_MODE = "night_mode"
        const val NIGHT_MODE_SYSTEM = "system"
        const val NIGHT_MODE_AUTO = "auto"
        const val NIGHT_MODE_OFF = "off"
        const val NIGHT_MODE_ON = "on"
        const val GRID_MODE = "grid_mode"
        const val GRID_MODE_GRID = "grid"
        const val GRID_MODE_STAGGERED_GRID = "staggered_grid"
        const val POST_LIMIT = "post_limit"
        const val POST_SIZE_BROWSE = "post_size_browse"
        const val POST_SIZE_DOWNLOAD = "post_size_download"
        const val POST_SIZE_SAMPLE = "sample"
        const val POST_SIZE_LARGER = "larger"
        const val POST_SIZE_ORIGIN = "origin"
        const val CACHE_MEMORY = "cache_memory"
        const val CACHE_DISK = "cache_disk"
        const val ACTIVE_PROFILE_ID = "active_profile_id"
        const val ACTIVE_PROFILE_SCHEME = "active_profile_scheme"
        const val ACTIVE_PROFILE_HOST = "active_profile_host"
        const val SPAN_COUNT = "span_count"
        const val IS_NOT_MORE_DATA = "not_more_data"
        const val IS_CHANGED_NIGHT_MODE = "changed_night_mode"
        const val ENABLE_CRASH_REPORT = "enable_crash_report"
        const val SAFE_MODE = "safe_mode"
    }

    private val editor: SharedPreferences.Editor = sp.edit()

    private var nightModeString: String
        get() = sp.getString(NIGHT_MODE, NIGHT_MODE_SYSTEM)
        set(value) = editor.putString(NIGHT_MODE, value).apply()

    @AppCompatDelegate.NightMode
    val nightMode: Int
        get() = when (nightModeString) {
        NIGHT_MODE_AUTO -> AppCompatDelegate.MODE_NIGHT_AUTO
        NIGHT_MODE_OFF -> AppCompatDelegate.MODE_NIGHT_NO
        NIGHT_MODE_ON -> AppCompatDelegate.MODE_NIGHT_YES
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    var gridModeString: String
        get() = sp.getString(GRID_MODE, GRID_MODE_STAGGERED_GRID)
        set(value) = editor.putString(GRID_MODE, value).apply()

    var postLimitInt: Int
        get() = sp.getString(POST_LIMIT, "50").toInt()
        set(value) = editor.putString(POST_LIMIT, value.toString()).apply()

    var cacheMemoryInt: Int
        get() = sp.getString(CACHE_MEMORY, "256").toInt()
        set(value) = editor.putString(CACHE_MEMORY, value.toString()).apply()

    var cacheDiskInt: Int
        get() = sp.getString(CACHE_DISK, "256").toInt()
        set(value) = editor.putString(CACHE_DISK, value.toString()).apply()

    var activeProfileId: Long
        get() = sp.getLong(ACTIVE_PROFILE_ID, 0L)
        set(value) = editor.putLong(ACTIVE_PROFILE_ID, value).apply()


    var activeProfileHost: String
        get() = sp.getString(ACTIVE_PROFILE_HOST, "mash.im")
        set(value) = editor.putString(ACTIVE_PROFILE_HOST, value).apply()

    var activeProfileSchema: String
        get() = sp.getString(ACTIVE_PROFILE_SCHEME, "https")
        set(value) = editor.putString(ACTIVE_PROFILE_SCHEME, value).apply()

    var spanCountInt: Int
        get() = sp.getInt(SPAN_COUNT, 3)
        set(value) = editor.putInt(SPAN_COUNT, value).apply()

    var isNotMoreData: Boolean
        get() = sp.getBoolean(IS_NOT_MORE_DATA, true)
        set(value) = editor.putBoolean(IS_NOT_MORE_DATA, value).apply()

    var isChangedNightMode: Boolean
        get() = sp.getBoolean(IS_CHANGED_NIGHT_MODE, false)
        set(value) = editor.putBoolean(IS_CHANGED_NIGHT_MODE, value).apply()

    var enabledCrashReport: Boolean
        get() = sp.getBoolean(ENABLE_CRASH_REPORT, true)
        set(value) = editor.putBoolean(ENABLE_CRASH_REPORT, value).apply()

    var postSizeBrowse: String
        get() = sp.getString(POST_SIZE_BROWSE, POST_SIZE_SAMPLE)
        set(value) = editor.putString(POST_SIZE_BROWSE, value).apply()

    var postSizeDownload: String
        get() = sp.getString(POST_SIZE_DOWNLOAD, POST_SIZE_LARGER)
        set(value) = editor.putString(POST_SIZE_DOWNLOAD, value).apply()

    var safeMode: Boolean
        get() = sp.getBoolean(SAFE_MODE, true)
        set(value) = editor.putBoolean(SAFE_MODE, value).apply()
}