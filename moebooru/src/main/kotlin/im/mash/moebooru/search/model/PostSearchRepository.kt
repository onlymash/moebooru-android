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

package im.mash.moebooru.search.model

import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

class PostSearchRepository(private val local: PostSearchDataContract.Local,
                 private val remote: PostSearchDataContract.Remote,
                 private val scheduler: Scheduler,
                 private val compositeDisposable: CompositeDisposable) : PostSearchDataContract.Repository {

    companion object {
        private const val TAG = "PostSearchRepository"
    }

    private var notMore = false
    override fun isNotMore(): Boolean = notMore

    override val postFetchOutcome: PublishSubject<Outcome<MutableList<PostSearch>>>
            = PublishSubject.create<Outcome<MutableList<PostSearch>>>()

    override val isEndOutCome: PublishSubject<Outcome<Boolean>>
            = PublishSubject.create<Outcome<Boolean>>()

    override fun fetchPosts(httpUrl: HttpUrl) {
        postFetchOutcome.loading(true)
        var tags = httpUrl.queryParameter("tags")
        if (tags == null) tags = ""
        //Observe changes to the db
        local.getPosts(httpUrl.host(), tags)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ posts ->
                    postFetchOutcome.success(posts)
                }, {
                    error ->
                    handleError(error)
                })
                .addTo(compositeDisposable)
    }

    override fun refreshPosts(httpUrl: HttpUrl) {
        logi(TAG, "refreshing")
        var tags = httpUrl.queryParameter("tags")
        if (tags == null) tags = ""
        notMore = false
        remote.getPosts(httpUrl)
                .performOnBackOutOnMain(scheduler)
                .subscribe(
                        { posts ->
                            val limit = httpUrl.queryParameter("limit")!!.toInt()
                            if (posts != null && posts.size > 0) {
                                if (posts.size < limit) {
                                    notMore = true
                                }
                                posts.forEach { post ->
                                    post.site = httpUrl.host()
                                    post.keyword = tags
                                }
                                savePosts(httpUrl.host(), posts, tags, limit)
                            } else {
                                notMore = true
                                handleError(Throwable("null"))
                            }
                            isEndOutCome.success(true)
                        }, { error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun loadMorePosts(httpUrl: HttpUrl) {
        if (notMore) {
            return
        }
        var tags = httpUrl.queryParameter("tags")
        if (tags == null) tags = ""
        remote.getPosts(httpUrl)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ posts ->
                    val limit = httpUrl.queryParameter("limit")!!.toInt()
                    if (posts != null && posts.size > 0) {
                        posts.forEach { post ->
                            post.site = httpUrl.host()
                            post.keyword = tags
                        }
                        addPosts(posts)
                    } else {
                        handleError(Throwable("null"))
                    }
                    if (posts == null || posts.size < limit) {
                        notMore = true
                    }
                    isEndOutCome.success(true)
                }, { error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    private fun savePosts(site: String, posts: MutableList<PostSearch>, tags: String, limit: Int) {
        Completable.fromAction{
            deletePosts(site, tags, limit)
        }
                .performOnBack(scheduler)
                .doOnComplete {
                    addPosts(posts)
                }
                .subscribe()
    }

    override fun deletePosts(site: String, tags: String, limit: Int) {
        local.deletePosts(site, tags, limit)
    }

    override fun addPosts(posts: MutableList<PostSearch>) {
        local.addPosts(posts)
    }

    override fun handleError(error: Throwable) {
        postFetchOutcome.failed(error)
    }
}