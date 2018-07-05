package im.mash.moebooru.common.model

import im.mash.moebooru.common.data.local.entity.Comment
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

class CommentRepository(private val local: CommentDataContract.Local,
                        private val remote: CommentDataContract.Remote,
                        private val scheduler: Scheduler) : CommentDataContract.Repository {

    private val compositeDisposable = CompositeDisposable()

    override val commentOutcome: PublishSubject<Outcome<MutableList<Comment>>>
        = PublishSubject.create<Outcome<MutableList<Comment>>>()

    override fun loadComment(host: String, commentId: Int) {
        commentOutcome.loading(true)
        local.loadComment(host, commentId)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ comments ->
                    commentOutcome.success(comments)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun loadComments(host: String, postId: Int) {
        commentOutcome.loading(true)
        local.loadComments(host, postId)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ comments ->
                    commentOutcome.success(comments)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun loadComments(host: String) {
        commentOutcome.loading(true)
        local.loadComments(host)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ comments ->
                    commentOutcome.success(comments)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun refreshComments(url: HttpUrl) {
        remote.getComments(url)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ comments ->
                    val host = url.host()
                    comments.forEach { comment ->
                        comment.host = host
                    }
                    local.saveComments(comments)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun createComment(url: String, postId: Int, body: String, anonymous: Int, username: String, passwordHash: String) {
        remote.createComment(url, postId, body, anonymous, username, passwordHash)
                .performOnBackOutOnMain(scheduler)
                .subscribe()
    }

    override fun deleteComment(url: String, comment: Comment, username: String, passwordHash: String) {
        remote.destroyComment(url, comment.id, username, passwordHash)
                .performOnBackOutOnMain(scheduler)
                .subscribe({
                    local.deleteComment(comment)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun handleError(error: Throwable) {
        commentOutcome.failed(error)
    }
}