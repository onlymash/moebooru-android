package im.mash.moebooru.common.data.local.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "boorus", indices = [Index(value = ["id", "url"], unique = true)])
data class Booru(
        @PrimaryKey(autoGenerate = true)
        var uid: Int?,
        val id: Int,
        val name: String,
        val scheme: String,
        val host: String,
        val url: String
)