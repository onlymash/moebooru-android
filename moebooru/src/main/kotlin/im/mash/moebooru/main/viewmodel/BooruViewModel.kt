package im.mash.moebooru.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.model.BooruDataContract
import im.mash.moebooru.util.logi
import io.reactivex.disposables.CompositeDisposable

class BooruViewModel(private val repo: BooruDataContract.Repository,
                     private val compositeDisposable: CompositeDisposable) : ViewModel() {

    companion object {
        private const val TAG = "BooruViewModel"
    }
    val booruOutcome: LiveData<Outcome<MutableList<Booru>>> by lazy {
        repo.booruFetchOutcome.toLiveData(compositeDisposable)
    }

    fun loadBoorus() {
        logi(TAG, "LoadBoorus")
        repo.loadBoorus()
    }

    fun addBooru(booru: Booru) {
        logi(TAG, "AddBooru")
        repo.addBooru(booru)
    }

    fun addBoorus(boorus: MutableList<Booru>) {
        repo.addBoorus(boorus)
    }

    fun clear() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        MoeDH.destroyMainComponent()
    }
}