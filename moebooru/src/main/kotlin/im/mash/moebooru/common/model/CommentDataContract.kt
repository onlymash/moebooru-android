package im.mash.moebooru.common.model

import im.mash.moebooru.common.data.local.entity.Comment
import im.mash.moebooru.common.data.remote.entity.CommentResponse
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

interface CommentDataContract {

    interface Repository {
        val commentOutcome: PublishSubject<Outcome<MutableList<Comment>>>
        fun loadComment(host: String, commentId: Int)
        fun loadComments(host: String, postId: Int)
        fun loadComments(host: String)
        fun refreshComments(url: HttpUrl)
        fun createComment(url: String, postId: Int, body: String, anonymous: Int, username: String, passwordHash: String)
        fun deleteComment(url: String, commentId: Int, username: String, passwordHash: String)
        fun handleError(error: Throwable)
    }

    interface Local {
        fun loadComment(host: String, commentId: Int): Flowable<MutableList<Comment>>
        fun loadComments(host: String, postId: Int): Flowable<MutableList<Comment>>
        fun loadComments(host: String): Flowable<MutableList<Comment>>
        fun saveComment(comment: Comment)
        fun saveComments(comments: MutableList<Comment>)
        fun deleteComment(host: String, commentId: Int)
    }

    interface Remote {
        fun getComments(url: HttpUrl): Single<MutableList<Comment>>
        fun createComment(url: String, postId: Int, body: String, anonymous: Int, username: String, passwordHash: String): Single<CommentResponse>
        fun destroyComment(url: String, commentId: Int, username: String, passwordHash: String): Single<CommentResponse>
    }
}