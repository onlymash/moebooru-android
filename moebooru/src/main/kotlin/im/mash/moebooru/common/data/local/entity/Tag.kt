package im.mash.moebooru.common.data.local.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tags", indices = [(Index(value = ["uid", "tag", "site"], unique = true))])
data class Tag(
        @PrimaryKey(autoGenerate = true)
        val uid: Int,
        val site: String,
        val tag: String,
        val is_selected: Boolean
)