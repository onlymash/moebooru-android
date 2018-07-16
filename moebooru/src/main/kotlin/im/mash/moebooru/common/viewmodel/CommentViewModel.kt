package im.mash.moebooru.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.Comment
import im.mash.moebooru.common.model.CommentDataContract
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.disposables.CompositeDisposable
import okhttp3.HttpUrl

class CommentViewModel(private val repo: CommentDataContract.Repository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val commentOutcome: LiveData<Outcome<MutableList<Comment>>> by lazy {
        repo.commentOutcome.toLiveData(compositeDisposable)
    }

    fun loadComment(host: String, commentId: Int) {
        repo.loadComment(host, commentId)
    }
    fun loadComments(host: String, postId: Int) {
        repo.loadComments(host, postId)
    }
    fun loadComments(host: String) {
        repo.loadComments(host)
    }
    fun refreshComments(url: HttpUrl) {
        repo.refreshComments(url)
    }
    fun createComment(url: String, postId: Int, body: String, anonymous: Int, username: String, passwordHash: String) {
        repo.createComment(url, postId, body, anonymous, username, passwordHash)
    }
    fun deleteComment(url: String, commentId: Int, username: String, passwordHash: String) {
        repo.deleteComment(url, commentId, username, passwordHash)
    }
}