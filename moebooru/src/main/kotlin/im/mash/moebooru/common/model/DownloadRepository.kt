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
import im.mash.moebooru.common.data.local.entity.PostDownload
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class DownloadRepository(private val database: MoeDatabase,
                         private val scheduler: Scheduler,
                         private val compositeDisposable: CompositeDisposable) : DownloadDataContract.Repository {

    companion object {
        private const val TAG = "DownloadRepository"
    }

    override val downloadPostsOutcome: PublishSubject<Outcome<MutableList<PostDownload>>>
            = PublishSubject.create<Outcome<MutableList<PostDownload>>>()

    override fun loadPosts() {
        downloadPostsOutcome.loading(true)
        database.postDownloadDao()
                .loadAll()
                .performOnBackOutOnMain(scheduler)
                .subscribe({ data ->
                    downloadPostsOutcome.success(data)
                }, { error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun addPost(post: PostDownload) {
        Completable.fromAction{
            database.postDownloadDao().save(post)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deletePost(post: PostDownload) {
        Completable.fromAction{
            database.postDownloadDao().delete(post)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deletePosts(posts: MutableList<PostDownload>) {
        Completable.fromAction{
            database.postDownloadDao().delete(posts)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deleteAll() {
        Completable.fromAction{
            database.postDownloadDao().deleteAll()
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun handleError(error: Throwable) {
        downloadPostsOutcome.failed(error)
    }

}