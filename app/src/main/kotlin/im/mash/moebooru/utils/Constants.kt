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

import android.webkit.WebView
import im.mash.moebooru.App.Companion.app

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

    const val SPAN_COUNT = "span_count"
    const val IS_NOT_MORE_DATA = "not_more_data"
    const val IS_CHANGED_NIGHT_MODE = "changed_night_mode"

    const val ITEMS_DATA = "items_data"
    const val ITEM_POS = "item_position"
    const val ITEM_ID = "item_id"
    const val BUNDLE = "bundle"

    const val STATUS_BAR_HEIGHT = "status_bar_height"
}

object Net {
    const val USER_AGENT_KEY = "User-Agent"
    var USER_AGENT_INFO: String = WebView(app).settings.userAgentString
}

object SearchTagsTable {
    const val TABLE_NAME = "search_tags"
    const val ID_UNIQUE = "_id"
    const val SITE = "id"
    const val NAME = "name"
    const val IS_SELECTED = "is_selected"
}

object BoorusTable {
    const val TABLE_NAME = "boorus"
    const val ID_UNIQUE = "_id"
    const val ID = "id"
    const val NAME = "name"
    const val URL = "url"
}

object PostsTable {
    const val TABLE_NAME = "posts"
    const val ID_UNIQUE = "_id"
    const val SITE = "site"
    const val ID = "id"
    const val TAGS = "tags"
    const val CREATE_AT = "created_at"
    const val CREATOR_ID = "creator_id"
    const val AUTHOR = "author"
    const val CHANGE = "change"
    const val SOURCE = "source"
    const val SCORE = "score"
    const val MD5 = "md5"
    const val FILE_SIZE = "file_size"
    const val FILE_URL = "file_url"
    const val IS_SHOWN_IN_INDEX = "is_shown_in_index"
    const val PREVIEW_URL = "preview_url"
    const val PREVIEW_WIDTH = "preview_width"
    const val PREVIEW_HEIGHT = "preview_height"
    const val ACTUAL_PREVIEW_WIDTH = "actual_preview_width"
    const val ACTUAL_PREVIEW_HEIGHT = "actual_preview_height"
    const val SAMPLE_URL = "sample_url"
    const val SAMPLE_WIDTH = "sample_width"
    const val SAMPLE_HEIGHT = "sample_height"
    const val SAMPLE_FILE_SIZE = "sample_file_size"
    const val JPEG_URL = "jpeg_url"
    const val JPEG_WIDTH = "jpeg_width"
    const val JPEG_HEIGHT = "jpeg_height"
    const val JPEG_FILE_SIZE = "jpeg_file_size"
    const val RATING = "rating"
    const val HAS_CHILDRE = "has_childre"
    const val PARENT_ID = "parent_id"
    const val STATUS = "status"
    const val WIDTH = "width"
    const val HEIGHT = "height"
    const val IS_HELD = "is_held"
}