package im.mash.moebooru.common.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tags", indices = [(Index(value = ["tag", "site"], unique = true))])
data class Tag(
        @PrimaryKey(autoGenerate = true)
        val uid: Int?,
        val site: String,
        var tag: String,
        var is_selected: Boolean
)