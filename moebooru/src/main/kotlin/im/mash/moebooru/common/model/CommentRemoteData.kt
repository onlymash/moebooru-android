package im.mash.moebooru.common.model

import im.mash.moebooru.common.data.local.entity.Comment
import im.mash.moebooru.common.data.remote.CommentService
import im.mash.moebooru.common.data.remote.entity.CommentResponse
import io.reactivex.Single
import okhttp3.HttpUrl

class CommentRemoteData(private val commentService: CommentService) : CommentDataContract.Remote {

    override fun getComments(url: HttpUrl): Single<MutableList<Comment>> {
        return commentService.getComments(url)
    }

    override fun createComment(url: String, postId: Int, body: String, anonymous: Int, username: String, passwordHash: String): Single<CommentResponse> {
        return commentService.createComment(url, postId, body, anonymous, username, passwordHash)
    }

    override fun destroyComment(url: String, commentId: Int, username: String, passwordHash: String): Single<CommentResponse> {
        return commentService.destroyComment(url, commentId, username, passwordHash)
    }
}