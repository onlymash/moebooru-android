package im.mash.moebooru.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.model.PostDataContract
import im.mash.moebooru.util.logi
import io.reactivex.disposables.CompositeDisposable
import okhttp3.HttpUrl

class PostViewModel(private val repo: PostDataContract.Repository,
                    private val compositeDisposable: CompositeDisposable) : ViewModel() {

    companion object {
        private const val TAG = "PostViewModel"
    }

    val postsOutcome: LiveData<Outcome<MutableList<Post>>> by lazy {
        //Convert publish subject to livedata
        repo.postFetchOutcome.toLiveData(compositeDisposable)
    }

    val isEndOutcome: LiveData<Outcome<Boolean>> by lazy {
        repo.isEndOutCome.toLiveData(compositeDisposable)
    }

    fun isNotMore(): Boolean {
        return repo.isNotMore()
    }

    fun loadPosts(httpUrl: HttpUrl) {
        repo.fetchPosts(httpUrl)
    }

    fun refreshPosts(httpUrl: HttpUrl) {
        repo.refreshPosts(httpUrl)
    }

    fun loadMorePosts(httpUrl: HttpUrl) {
        repo.loadMorePosts(httpUrl)
    }

    fun deletePosts(site: String, limit: Int) {
        repo.deletePosts(site, limit)
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