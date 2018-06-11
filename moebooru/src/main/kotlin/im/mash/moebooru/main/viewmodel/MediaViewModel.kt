package im.mash.moebooru.main.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import im.mash.moebooru.common.data.media.entity.MediaStoreData
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.model.MediaDataContract
import io.reactivex.disposables.CompositeDisposable

class MediaViewModel(private val repo: MediaDataContract.Repository,
                     private val compositeDisposable: CompositeDisposable) : ViewModel() {

    companion object {
        private const val TAG = "MediaViewModel"
    }

    val mediaOutcome: LiveData<Outcome<MutableList<MediaStoreData>>> by lazy {
        repo.mediaOutcome.toLiveData(compositeDisposable)
    }

    fun loadMedia(path: String) {
        repo.getMedia(path)
    }

    override fun onCleared() {
        super.onCleared()
        //clear the disposables when the viewmodel is cleared
        compositeDisposable.clear()
    }
}