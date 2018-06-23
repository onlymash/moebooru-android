package im.mash.moebooru.common.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import im.mash.moebooru.common.model.UserDataContract
import io.reactivex.disposables.CompositeDisposable

@Suppress("UNCHECKED_CAST")
class UserViewModelFactory(private val repository: UserDataContract.Repository,
                           private val compositeDisposable: CompositeDisposable) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel(repository, compositeDisposable) as T
    }
}