package im.mash.moebooru.common.data.local.dao

import android.arch.persistence.room.*
import im.mash.moebooru.common.data.local.entity.Comment
import io.reactivex.Flowable

@Dao
interface CommentDao {

    @Query("SELECT * FROM comments WHERE host = :host ORDER BY id DESC")
    fun getComments(host: String): Flowable<MutableList<Comment>>

    @Query("SELECT * FROM comments WHERE host = :host AND post_id = :postId ORDER BY id DESC")
    fun getCommentsByPostId(host: String, postId: Int): Flowable<MutableList<Comment>>

    @Query("SELECT * FROM comments WHERE host = :host AND id = :commentId ORDER BY id DESC")
    fun getCommentByCommentId(host: String, commentId: Int): Flowable<MutableList<Comment>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertComment(comment: Comment)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertComments(comment: MutableList<Comment>)

    @Delete
    fun deleteComment(comment: Comment)

    @Delete
    fun deleteComments(comment: MutableList<Comment>)
}