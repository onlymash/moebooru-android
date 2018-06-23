package im.mash.moebooru.common.data.local.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "user", indices = [(Index(value = ["site"], unique = true))])
data class User(
        @PrimaryKey(autoGenerate = true)
        var uid: Int?,
        var site: String?,
        var name: String,
        var blacklisted_tags: String,
        var id: Int,
        var password_hash: String?
)