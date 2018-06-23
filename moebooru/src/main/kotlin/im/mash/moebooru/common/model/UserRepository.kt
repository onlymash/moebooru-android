package im.mash.moebooru.common.model

import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.User
import im.mash.moebooru.common.data.remote.UserService
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

class UserRepository(private val database: MoeDatabase,
                     private val userService: UserService,
                     private val scheduler: Scheduler,
                     private val compositeDisposable: CompositeDisposable) : UserDataContract.Repository {

    companion object {
        private const val TAG = "UserRepository"
    }

    override val userOutcome: PublishSubject<Outcome<MutableList<User>>> = PublishSubject.create<Outcome<MutableList<User>>>()

    override fun loadUser(site: String) {
        userOutcome.loading(true)
        database.userDao().loadUser(site)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ users ->
                    userOutcome.success(users)
                }, {error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun getUser(httpUrl: HttpUrl, passwordHash: String) {
        userService.getUser(httpUrl)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ rawUsers ->
                    logi(TAG, rawUsers.toString())
                    if (rawUsers.size == 1) {
                        val user = User(null, httpUrl.host(), rawUsers[0].name,
                                rawUsers[0].blacklisted_tags.toString().replace("[", "").replace("]", ""),
                                rawUsers[0].id, passwordHash)
                        saveUser(user)
                    } else {
                        userOutcome.loading(true)
                        userOutcome.success(mutableListOf())
                    }
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun saveUser(user: User) {
        Completable.fromAction { database.userDao().insertUser(user) }
                .performOnBackOutOnMain(scheduler)
                .subscribe({}, { error -> handleError(error)})
    }

    override fun deleteUser(user: User) {
        Completable.fromAction { database.userDao().deleteUser(user) }
                .performOnBackOutOnMain(scheduler)
                .subscribe({}, { error -> handleError(error)})
    }

    override fun handleError(error: Throwable) {
        userOutcome.failed(error)
    }
}