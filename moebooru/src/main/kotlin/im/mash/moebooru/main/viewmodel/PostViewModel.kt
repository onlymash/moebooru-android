package im.mash.moebooru.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.network.Outcome
import im.mash.moebooru.main.model.PostDataContract
import io.reactivex.disposables.CompositeDisposable
import okhttp3.HttpUrl

class PostViewModel(private val repo: PostDataContract.Repository,
                    private val compositeDisposable: CompositeDisposable) : ViewModel() {

    val postsOutcome: LiveData<Outcome<MutableList<Post>>> by lazy {
        //Convert publish subject to livedata
        repo.postFetchOutcome.toLiveData(compositeDisposable)
    }

    fun isNotMore(): Boolean {
        return repo.isNotMore()
    }

    fun loadPosts(httpUrl: HttpUrl) {
        if (postsOutcome.value == null)
            repo.fetchPosts(httpUrl)
    }

    fun reLoadPosts(httpUrl: HttpUrl) {
        repo.fetchPosts(httpUrl)
    }

    fun refreshPosts(httpUrl: HttpUrl) {
        repo.refreshPosts(httpUrl)
    }

    fun loadMorePosts(httpUrl: HttpUrl) {
        repo.loadMorePosts(httpUrl)
    }

    fun clear() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        //clear the disposables when the viewmodel is cleared
        compositeDisposable.clear()
    }
}