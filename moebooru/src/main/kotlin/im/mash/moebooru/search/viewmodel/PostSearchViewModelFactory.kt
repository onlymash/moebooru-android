package im.mash.moebooru.search.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import im.mash.moebooru.search.model.PostSearchDataContract
import io.reactivex.disposables.CompositeDisposable

@Suppress("UNCHECKED_CAST")
class PostSearchViewModelFactory(private val repository: PostSearchDataContract.Repository,
                                 private val compositeDisposable: CompositeDisposable) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostSearchViewModel(repository, compositeDisposable) as T
    }
}