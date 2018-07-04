package im.mash.moebooru.common.data.local.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "pools", indices = [(Index(value = ["id"], unique = true))])
data class Pool(
        @PrimaryKey(autoGenerate = true)
        var uid: Int?,
        var host: String?,
        val id: Int,
        val name: String,
        val created_at: String,
        val user_id: Int,
        val is_public: Boolean,
        val post_count: Int,
        val description: String?
)