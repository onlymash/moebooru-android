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

import android.os.Parcel
import android.os.Parcelable

data class RawPost(
        val id: Long?,
        val tags: String?,
        val created_at: Long?,
        val creator_id: Long?,
        val author: String?,
        val change: Long?,
        val source: String?,
        val score: Long?,
        val md5: String?,
        val file_size: Long?,
        val file_url: String?,
        val is_shown_in_index: Boolean?,
        val preview_url: String?,
        val preview_width: Long?,
        val preview_height: Long?,
        val actual_preview_width: Long?,
        val actual_preview_height: Long?,
        val sample_url: String?,
        val sample_width: Long?,
        val sample_height: Long?,
        val sample_file_size: Long?,
        val jpeg_url: String?,
        val jpeg_width: Long?,
        val jpeg_height: Long?,
        val jpeg_file_size: Long?,
        val rating: String?,
        val has_children: Boolean?,
        val parent_id: Long?,
        val status: String?,
        val width: Long?,
        val height: Long?,
        val is_held: Boolean?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(tags)
        parcel.writeValue(created_at)
        parcel.writeValue(creator_id)
        parcel.writeString(author)
        parcel.writeValue(change)
        parcel.writeString(source)
        parcel.writeValue(score)
        parcel.writeString(md5)
        parcel.writeValue(file_size)
        parcel.writeString(file_url)
        parcel.writeValue(is_shown_in_index)
        parcel.writeString(preview_url)
        parcel.writeValue(preview_width)
        parcel.writeValue(preview_height)
        parcel.writeValue(actual_preview_width)
        parcel.writeValue(actual_preview_height)
        parcel.writeString(sample_url)
        parcel.writeValue(sample_width)
        parcel.writeValue(sample_height)
        parcel.writeValue(sample_file_size)
        parcel.writeString(jpeg_url)
        parcel.writeValue(jpeg_width)
        parcel.writeValue(jpeg_height)
        parcel.writeValue(jpeg_file_size)
        parcel.writeString(rating)
        parcel.writeValue(has_children)
        parcel.writeValue(parent_id)
        parcel.writeString(status)
        parcel.writeValue(width)
        parcel.writeValue(height)
        parcel.writeValue(is_held)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RawPost> {
        override fun createFromParcel(parcel: Parcel): RawPost {
            return RawPost(parcel)
        }

        override fun newArray(size: Int): Array<RawPost?> {
            return arrayOfNulls(size)
        }
    }
}
