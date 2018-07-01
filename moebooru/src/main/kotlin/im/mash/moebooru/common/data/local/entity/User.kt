package im.mash.moebooru.common.data.local.entity

import android.arch.persistence.room.*
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