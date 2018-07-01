package im.mash.moebooru.common.model

import im.mash.moebooru.common.data.local.entity.User
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

interface UserDataContract {
    interface Repository {
        val userOutcome: PublishSubject<Outcome<MutableList<User>>>
        fun loadUsers()
        fun saveUser(user: User)
        fun deleteUser(user: User)
        fun updateUser(user: User)
        fun getUser(httpUrl: HttpUrl, passwordHash: String)
        fun handleError(error: Throwable)
    }
}