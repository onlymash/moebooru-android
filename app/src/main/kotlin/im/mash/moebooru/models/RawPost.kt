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

package im.mash.moebooru.models

data class RawPost(
        var actual_preview_height: Int,
        var actual_preview_width: Int,
        var author: String,
        var change: Int,
        var created_at: Long,
        var creator_id: Long,
        var file_size: Int,
        var file_url: String,
        var has_children: Boolean,
        var height: Int,
        var id: Long,
        var is_held: Boolean,
        var is_shown_in_index: Boolean,
        var jpeg_file_size: Int,
        var jpeg_height: Int,
        var jpeg_url: String,
        var jpeg_width: Int,
        var md5: String,
        var parent_id: Long,
        var preview_height: Int,
        var preview_url: String,
        var preview_width: Int,
        var rating: String,
        var sample_file_size: Int,
        var sample_height: Int,
        var sample_url: String,
        var sample_width: Int,
        var score: Int,
        var source: String,
        var status: String,
        var tags: String,
        var width: Int
)
