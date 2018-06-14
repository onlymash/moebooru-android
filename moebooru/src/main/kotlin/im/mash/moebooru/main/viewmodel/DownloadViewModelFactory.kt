package im.mash.moebooru.main.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import im.mash.moebooru.main.model.BooruDataContract
import im.mash.moebooru.main.model.DownloadDataContract
import io.reactivex.disposables.CompositeDisposable

@Suppress("UNCHECKED_CAST")
class DownloadViewModelFactory(private val repository: DownloadDataContract.Repository,
                               private val compositeDisposable: CompositeDisposable) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DownloadViewModel(repository, compositeDisposable) as T
    }
}