package im.mash.moebooru.common.data.local.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "posts_download", indices = [(Index(value = ["id", "url"], unique = true))])
data class PostDownload(
        @PrimaryKey(autoGenerate = true)
        var uid: Int?,
        val domain: String,
        val id: Int,
        val preview_url: String,
        val url: String,
        var status: String = ""
)