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

package im.mash.moebooru.detail.model

import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class DetailRepository(private val local: DetailDataContract.Local,
                       private val scheduler: Scheduler,
                       private val compositeDisposable: CompositeDisposable) : DetailDataContract.Repository {

    companion object {
        private const val TAG = "DetailRepository"
    }

    override val postFetchOutcome: PublishSubject<Outcome<MutableList<Post>>>
        = PublishSubject.create<Outcome<MutableList<Post>>>()

    override val postSearchFetchOutcome: PublishSubject<Outcome<MutableList<PostSearch>>>
        = PublishSubject.create<Outcome<MutableList<PostSearch>>>()

    override fun fetchPosts(site: String) {
        postFetchOutcome.loading(true)
        local.getPosts(site)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ posts ->
                    postFetchOutcome.success(posts)
                }, { error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun fetchPosts(site: String, tags: String) {
        postSearchFetchOutcome.loading(true)
        local.getPosts(site, tags)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ posts ->
                    logi(TAG, posts.size.toString())
                    postSearchFetchOutcome.success(posts)
                }, { error -> handleSearchError(error) })
                .addTo(compositeDisposable)
    }

    override fun handleError(error: Throwable) {
        postFetchOutcome.failed(error)
    }

    override fun handleSearchError(error: Throwable) {
        postSearchFetchOutcome.failed(error)
    }
}