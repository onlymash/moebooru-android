package im.mash.moebooru.common.data.local.dao

import android.arch.persistence.room.*
import im.mash.moebooru.common.data.local.entity.PostSearch
import io.reactivex.Flowable

@Dao
interface PostSearchDao {

    @Query("SELECT * FROM posts_search")
    fun loadAll(): Flowable<MutableList<PostSearch>>

    @Query("SELECT * FROM posts_search WHERE site=:site AND id=:id")
    fun getPost(site: String, id: Int): Flowable<PostSearch>

    @Query("SELECT * FROM posts_search WHERE site=:site")
    fun getPosts(site: String): Flowable<MutableList<PostSearch>>

    @Query("SELECT * FROM posts_search WHERE site=:site AND keyword = :tags")
    fun getPosts(site: String, tags: String): Flowable<MutableList<PostSearch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(posts: MutableList<PostSearch>)

    @Delete
    fun delete(postSearch: PostSearch)
}