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
        val id: Long?,
        val tags: String?,
        val created_at: Long?,
        val creator_id: Long?,
        val author: String?,
        val change: Long?,
        val source: String?,
        val score: Int?,
        val md5: String?,
        val file_size: Int?,
        val file_url: String?,
        val is_shown_in_index: Boolean?,
        val preview_url: String?,
        val preview_width: Int?,
        val preview_height: Int?,
        val actual_preview_width: Int?,
        val actual_preview_height: Int?,
        val sample_url: String?,
        val sample_width: Int?,
        val sample_height: Int?,
        val sample_file_size: Int?,
        val jpeg_url: String?,
        val jpeg_width: Int?,
        val jpeg_height: Int?,
        val jpeg_file_size: Int?,
        val rating: String?,
        val has_children: Boolean?,
        val parent_id: Long?,
        val status: String?,
        val width: Int?,
        val height: Int?,
        val is_held: Boolean?
)
