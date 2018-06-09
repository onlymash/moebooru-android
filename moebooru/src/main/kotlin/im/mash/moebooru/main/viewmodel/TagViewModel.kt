package im.mash.moebooru.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.Tag
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.network.Outcome
import im.mash.moebooru.main.model.TagDataContract
import io.reactivex.disposables.CompositeDisposable

class TagViewModel(private val repo: TagDataContract.Repository,
                   private val compositeDisposable: CompositeDisposable) : ViewModel() {

    val tagOutcome: LiveData<Outcome<MutableList<Tag>>> by lazy {
        repo.tagFetchOutcome.toLiveData(compositeDisposable)
    }

    fun loadTags(site: String) {
        if (tagOutcome.value == null) {
            repo.getTags(site)
        }
    }

    fun reLoadTags(site: String) {
        repo.getTags(site)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
