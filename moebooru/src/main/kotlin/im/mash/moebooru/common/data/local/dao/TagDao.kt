package im.mash.moebooru.common.data.local.dao

import android.arch.persistence.room.*
import im.mash.moebooru.common.data.local.entity.Tag
import io.reactivex.Flowable

@Dao
interface TagDao {
    @Query("SELECT * FROM tags WHERE site = :site")
    fun getTags(site: String): Flowable<MutableList<Tag>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTag(tag: Tag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTags(tags: MutableList<Tag>)

    @Delete
    fun deleteTag(tag: Tag)
}