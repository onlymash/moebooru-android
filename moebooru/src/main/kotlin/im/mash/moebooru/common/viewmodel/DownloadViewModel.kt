package im.mash.moebooru.common.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.PostDownload
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.common.model.DownloadDataContract
import io.reactivex.disposables.CompositeDisposable

class DownloadViewModel(private val repo: DownloadDataContract.Repository,
                        private val compositeDisposable: CompositeDisposable) : ViewModel() {

    companion object {
        private const val TAG = "DownloadViewModel"
    }

    val downloadPostsOutcome: LiveData<Outcome<MutableList<PostDownload>>> by lazy {
        repo.downloadPostsOutcome.toLiveData(compositeDisposable)
    }

    fun loadAll() {
        repo.loadPosts()
    }

    fun delete(post: PostDownload) {
        repo.deletePost(post)
    }

    fun delete(posts: MutableList<PostDownload>) {
        repo.deletePosts(posts)
    }

    fun deleteAll() {
        repo.deleteAll()
    }

    fun addTask(post: PostDownload) {
        repo.addPost(post)
    }

    override fun onCleared() {
        super.onCleared()
        //clear the disposables when the viewmodel is cleared
        compositeDisposable.clear()
    }
}