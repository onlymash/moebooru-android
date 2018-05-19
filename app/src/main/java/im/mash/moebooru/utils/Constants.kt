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

object Key {
    const val NIGHT_MODE = "night_mode"
    const val NIGHT_MODE_SYSTEM = "system"
    const val NIGHT_MODE_AUTO = "auto"
    const val NIGHT_MODE_OFF = "off"
    const val NIGHT_MODE_ON = "on"

    const val GRID_MODE = "grid_mode"
    const val GRID_MODE_GRID = "grid"
    const val GRID_MODE_STAGGERED_GRID = "staggered_grid"

    const val POST_LIMIT = "post_limit"
    const val CACHE_MEMORY = "cache_memory"
    const val CACHE_DISK = "cache_disk"

    const val ACTIVE_PROFILE = "active_profile"
}

object BoorusTable {
    const val TABLE_NAME = "boorus"
    const val ID = "_id"
    const val NAME = "name"
    const val URL = "url"
}