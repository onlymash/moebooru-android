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

package im.mash.moebooru.common.data.local.entity

import androidx.room.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.*

@Entity(tableName = "user",
        indices = [(Index(value = ["url"], unique = true))],
        foreignKeys = [(ForeignKey(
                entity = Booru::class,
                parentColumns = ["url"],
                childColumns = ["url"],
                onDelete = ForeignKey.CASCADE))])
data class User(
        @PrimaryKey(autoGenerate = true)
        var uid: Int?,
        var url: String,
        var name: String,
        var blacklisted_tags: String,
        var id: Int,
        var password_hash: String,
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        var avatar: ByteArray?
) {

        fun getAvatarBitmap(): Bitmap? {
                val avatar = this.avatar ?: return null
                return BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
        }

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as User

                if (uid != other.uid) return false
                if (url != other.url) return false
                if (name != other.name) return false
                if (blacklisted_tags != other.blacklisted_tags) return false
                if (id != other.id) return false
                if (password_hash != other.password_hash) return false
                if (!Arrays.equals(avatar, other.avatar)) return false

                return true
        }

        override fun hashCode(): Int {
                var result = uid ?: 0
                result = 31 * result + url.hashCode()
                result = 31 * result + name.hashCode()
                result = 31 * result + blacklisted_tags.hashCode()
                result = 31 * result + id
                result = 31 * result + password_hash.hashCode()
                result = 31 * result + (avatar?.let { Arrays.hashCode(it) } ?: 0)
                return result
        }

}