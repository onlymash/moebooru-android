package im.mash.moebooru.common.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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