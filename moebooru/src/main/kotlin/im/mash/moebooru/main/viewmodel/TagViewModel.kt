package im.mash.moebooru.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.Tag
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.model.TagDataContract
import io.reactivex.disposables.CompositeDisposable

class TagViewModel(private val repo: TagDataContract.Repository,
                   private val compositeDisposable: CompositeDisposable) : ViewModel() {

    val tagOutcome: LiveData<Outcome<MutableList<Tag>>> by lazy {
        repo.tagFetchOutcome.toLiveData(compositeDisposable)
    }

    fun loadTags(site: String) {
        repo.getTags(site)
    }

    fun deleteTag(tag: Tag) {
        repo.deleteTag(tag)
    }

    fun saveTag(tag: Tag) {
        repo.saveTag(tag)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
