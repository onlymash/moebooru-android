package im.mash.moebooru.common.data.local.dao

import android.arch.persistence.room.*
import im.mash.moebooru.common.data.local.entity.Post
import io.reactivex.Flowable

@Dao
interface PostDao {

    @Query("SELECT * FROM posts")
    fun getAll(): Flowable<MutableList<Post>>

    @Query("SELECT * FROM posts WHERE site = :site AND id = :id")
    fun getPost(site: String, id: Int): Flowable<Post>

    @Query("SELECT * FROM posts WHERE site = :site ORDER BY id DESC")
    fun getPosts(site: String): Flowable<MutableList<Post>>

    @Query("DELETE FROM posts WHERE site = :site")
    fun deletePosts(site: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(posts: MutableList<Post>)

    @Delete
    fun delete(post: Post)
}