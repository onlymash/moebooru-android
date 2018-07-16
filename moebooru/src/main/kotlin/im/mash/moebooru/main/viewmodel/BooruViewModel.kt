package im.mash.moebooru.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.model.BooruDataContract
import im.mash.moebooru.util.logi
import io.reactivex.disposables.CompositeDisposable

class BooruViewModel(private val repo: BooruDataContract.Repository) : ViewModel() {

    companion object {
        private const val TAG = "BooruViewModel"
    }

    private val compositeDisposable = CompositeDisposable()

    val booruOutcome: LiveData<Outcome<MutableList<Booru>>> by lazy {
        repo.booruFetchOutcome.toLiveData(compositeDisposable)
    }

    fun loadBoorus() {
        repo.loadBoorus()
    }

    fun addBooru(booru: Booru) {
        repo.addBooru(booru)
    }

    fun addBoorus(boorus: MutableList<Booru>) {
        repo.addBoorus(boorus)
    }

    fun deleteBooru(booru: Booru) {
        repo.deleteBooru(booru)
    }

}