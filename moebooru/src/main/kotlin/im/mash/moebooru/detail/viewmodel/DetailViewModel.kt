package im.mash.moebooru.detail.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.network.Outcome
import im.mash.moebooru.detail.model.DetailDataContract
import io.reactivex.disposables.CompositeDisposable

class DetailViewModel(private val repo: DetailDataContract.Repository,
                      private val compositeDisposable: CompositeDisposable) : ViewModel() {

    val postOutcome: LiveData<Outcome<MutableList<Post>>> by lazy {
        repo.postFetchOutcome.toLiveData(compositeDisposable)
    }

    val postSearchOutcome: LiveData<Outcome<MutableList<PostSearch>>> by lazy {
        repo.postSearchFetchOutcome.toLiveData(compositeDisposable)
    }

    fun loadPosts(site: String) {
        repo.fetchPosts(site)
    }

    fun loadPosts(site: String, tags: String) {
        repo.fetchPosts(site, tags)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        MoeDH.destroyDetailComponent()
    }
}