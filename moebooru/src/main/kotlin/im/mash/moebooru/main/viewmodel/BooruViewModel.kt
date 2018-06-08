package im.mash.moebooru.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.network.Outcome
import im.mash.moebooru.main.model.BooruDataContract
import io.reactivex.disposables.CompositeDisposable

class BooruViewModel(private val repo: BooruDataContract.Repository,
                     private val compositeDisposable: CompositeDisposable) : ViewModel() {

    val booruOutcome: LiveData<Outcome<MutableList<Booru>>> by lazy {
        repo.booruFetchOutcome.toLiveData(compositeDisposable)
    }

    fun loadBoorus() {
        if (booruOutcome.value == null) {
            repo.loadBoorus()
        }
    }

    fun reLoadBoorus() {
        repo.loadBoorus()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        MoeDH.destroyMainComponent()
    }
}