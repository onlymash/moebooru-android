package im.mash.moebooru.common.data.local.dao

import android.arch.persistence.room.*
import im.mash.moebooru.common.data.local.entity.PostSearch
import io.reactivex.Flowable

@Dao
interface PostSearchDao {

    @Query("SELECT * FROM posts_search")
    fun getAll(): Flowable<MutableList<PostSearch>>

    @Query("SELECT * FROM posts_search WHERE site=:site AND keyword = :tags AND id = :id")
    fun getPost(site: String, tags: String, id: Int): Flowable<PostSearch>

    @Query("SELECT * FROM posts_search WHERE site=:site ORDER BY id DESC")
    fun getPosts(site: String): Flowable<MutableList<PostSearch>>

    @Query("SELECT * FROM posts_search WHERE site=:site AND keyword = :tags ORDER BY id DESC")
    fun getPosts(site: String, tags: String): Flowable<MutableList<PostSearch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(posts: MutableList<PostSearch>)

    @Query("DELETE FROM posts_search WHERE site = :site AND keyword = :tags")
    fun deletePosts(site: String, tags: String)

    @Delete
    fun delete(postSearch: PostSearch)
}