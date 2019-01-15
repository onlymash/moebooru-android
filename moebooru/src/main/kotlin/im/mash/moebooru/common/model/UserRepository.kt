/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

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

    override fun loadUsers() {
        userOutcome.loading(true)
        database.userDao().loadUsers()
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
                    val name = httpUrl.queryParameter("name").toString()
                    var user: User? = null
                    rawUsers.forEach { rawUser ->
                        if (rawUser.name == name) {
                            user = User(null, httpUrl.scheme() + "://" + httpUrl.host(), name,
                                    rawUser.blacklisted_tags.toString()
                                            .replace("[", "")
                                            .replace("]", ""),
                                    rawUser.id, passwordHash, null)
                            saveUser(user!!)
                            return@forEach
                        }
                    }
                    if (user == null) {
                        loadUsers()
                    }
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun updateUser(user: User) {
        Completable.fromAction { database.userDao().updateUser(user) }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun saveUser(user: User) {
        Completable.fromAction { database.userDao().insertUser(user) }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deleteUser(user: User) {
        Completable.fromAction { database.userDao().deleteUser(user) }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun handleError(error: Throwable) {
        userOutcome.failed(error)
    }
}