package im.mash.moebooru.common.data.local.dao

import android.arch.persistence.room.*
import im.mash.moebooru.common.data.local.entity.PostDownload
import io.reactivex.Flowable

@Dao
interface PostDownloadDao {

    @Query("SELECT * FROM posts_download")
    fun loadAll(): Flowable<MutableList<PostDownload>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(post: PostDownload)

    @Delete
    fun delete(post: PostDownload)

    @Delete
    fun delete(posts: MutableList<PostDownload>)

    @Query("DELETE FROM posts_download")
    fun deleteAll()
}