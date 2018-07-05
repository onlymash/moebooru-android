package im.mash.moebooru.common.data.local.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "comments", indices = [(Index(value = ["id"], unique = true))])
data class Comment(
        @PrimaryKey(autoGenerate = true)
        var uid: Int?,
        var host: String?,
        val id: Int,
        val created_at: String,
        val post_id: Int,
        val creator: String,
        val creator_id: Int,
        val body: String
)