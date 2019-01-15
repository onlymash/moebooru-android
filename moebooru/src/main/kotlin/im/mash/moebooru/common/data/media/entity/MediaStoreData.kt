/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package im.mash.moebooru.common.data.media.entity

import android.net.Uri

data class MediaStoreData(
        var rowId: Long,
        var uri: Uri,
        var mimeType: String,
        var dateTaken: Long,
        var dateModified: Long,
        var orientation: Int,
        var type: Type,
        var mediaData: String) {

    enum class Type {
        VIDEO,
        IMAGE
    }

    override fun toString(): String {
        return ("MediaStoreData{"
                + "rowId=" + rowId
                + ", uri=" + uri
                + ", mimeType='" + mimeType + '\''.toString()
                + ", dateModified=" + dateModified
                + ", orientation=" + orientation
                + ", type=" + type
                + ", dateTaken=" + dateTaken
                + ", mediaData=" + mediaData
                + '}'.toString())
    }
}