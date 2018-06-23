package im.mash.moebooru.common.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.User
import im.mash.moebooru.common.model.UserDataContract
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.disposables.CompositeDisposable
import okhttp3.HttpUrl

class UserViewModel(private val repository: UserDataContract.Repository,
                    private val compositeDisposable: CompositeDisposable) : ViewModel() {

    val userOutcome: LiveData<Outcome<MutableList<User>>> by lazy {
        repository.userOutcome.toLiveData(compositeDisposable)
    }

    fun loadUser(site: String) {
        repository.loadUser(site)
    }

    fun getUser(httpUrl: HttpUrl, passwordHash: String) {
        repository.getUser(httpUrl, passwordHash)
    }

    fun deleteUser(user: User) {
        repository.deleteUser(user)
    }

}