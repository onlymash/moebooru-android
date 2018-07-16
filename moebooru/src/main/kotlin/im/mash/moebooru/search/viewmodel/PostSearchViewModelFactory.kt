package im.mash.moebooru.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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