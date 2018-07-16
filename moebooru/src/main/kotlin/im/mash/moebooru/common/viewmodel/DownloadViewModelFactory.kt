package im.mash.moebooru.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import im.mash.moebooru.common.model.DownloadDataContract
import io.reactivex.disposables.CompositeDisposable

@Suppress("UNCHECKED_CAST")
class DownloadViewModelFactory(private val repository: DownloadDataContract.Repository,
                               private val compositeDisposable: CompositeDisposable) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DownloadViewModel(repository, compositeDisposable) as T
    }
}