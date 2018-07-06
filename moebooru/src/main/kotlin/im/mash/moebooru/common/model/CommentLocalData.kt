package im.mash.moebooru.common.model

import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.Comment
import im.mash.moebooru.core.extensions.performOnBack
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.Completable
import io.reactivex.Flowable

class CommentLocalData(private val database: MoeDatabase,
                       private val scheduler: Scheduler) : CommentDataContract.Local {

    override fun loadComment(host: String, commentId: Int): Flowable<MutableList<Comment>> {
        return database.commentDao().getCommentByCommentId(host, commentId)
    }

    override fun loadComments(host: String, postId: Int): Flowable<MutableList<Comment>> {
        return database.commentDao().getCommentsByPostId(host, postId)
    }

    override fun loadComments(host: String): Flowable<MutableList<Comment>> {
        return database.commentDao().getComments(host)
    }

    override fun saveComment(comment: Comment) {
        Completable.fromAction { database.commentDao().insertComment(comment) }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun saveComments(comments: MutableList<Comment>) {
        Completable.fromAction { database.commentDao().insertComments(comments) }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deleteComment(host: String, commentId: Int) {
        Completable.fromAction { database.commentDao().deleteCommentById(host, commentId) }
                .performOnBack(scheduler)
                .subscribe()
    }
}