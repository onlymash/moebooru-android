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
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.common.data.remote.VoteService
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.net.URL

class VoteRepository(private val voteService: VoteService,
                     private val database: MoeDatabase,
                     private val scheduler: Scheduler,
                     private val compositeDisposable: CompositeDisposable) : VoteDataContract.Repository {

    companion object {
        private const val TAG = "VoteRepository"
    }

    override val idsOutcomeOneTwo: PublishSubject<Outcome<MutableList<Int>>>
        = PublishSubject.create<Outcome<MutableList<Int>>>()

    override val idsOutcomeThree: PublishSubject<Outcome<MutableList<Int>>>
            = PublishSubject.create<Outcome<MutableList<Int>>>()

    override fun getVoteIdsOneTwo(site: String, username: String) {
        idsOutcomeOneTwo.loading(true)
        database.postSearchDao()
                .getPostsId(site, "vote:1:$username order:vote", "vote:2:$username order:vote")
                .performOnBackOutOnMain(scheduler)
                .subscribe({ ids ->
                    idsOutcomeOneTwo.success(ids)
                }, { error -> handleErrorOneTwo(error)})
                .addTo(compositeDisposable)
    }

    override fun getVoteIdsThree(site: String, username: String) {
        idsOutcomeThree.loading(true)
        database.postSearchDao()
                .getPostsId(site, "vote:3:$username order:vote")
                .performOnBackOutOnMain(scheduler)
                .subscribe({ ids ->
                    idsOutcomeThree.success(ids)
                }, { error -> handleErrorOneTwo(error)})
                .addTo(compositeDisposable)
    }

    override fun votePost(url: String, id: Int, score: Int, username: String, passwordHash: String) {
        voteService.votePost(url, id, score, username, passwordHash)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ vote ->
                    if (vote.success && vote.posts.size == 1) {
                        val post = vote.posts[0]
                        post.site = URL(url).host
                        if (score == 0) {
                            post.keyword = "vote:1:$username order:vote"
                            deleteVotePost(post.copy())
                            post.keyword = "vote:2:$username order:vote"
                            deleteVotePost(post.copy())
                            post.keyword = "vote:3:$username order:vote"
                            deleteVotePost(post.copy())
                        } else {
                            when (score) {
                                1 -> {
                                    post.keyword = "vote:2:$username order:vote"
                                    deleteVotePost(post.copy())
                                    post.keyword = "vote:3:$username order:vote"
                                    deleteVotePost(post.copy())
                                }
                                2 -> {
                                    post.keyword = "vote:1:$username order:vote"
                                    deleteVotePost(post.copy())
                                    post.keyword = "vote:3:$username order:vote"
                                    deleteVotePost(post.copy())
                                }
                                3 -> {
                                    post.keyword = "vote:1:$username order:vote"
                                    deleteVotePost(post.copy())
                                    post.keyword = "vote:2:$username order:vote"
                                    deleteVotePost(post.copy())
                                }
                            }
                            post.keyword = "vote:$score:$username order:vote"
                            saveVotePost(post)
                        }
                    }
                }) { error ->
                    when (score) {
                        1 -> handleErrorOneTwo(error)
                        2 -> handleErrorOneTwo(error)
                        3 -> handleErrorThree(error)
                        0 -> {
                            handleErrorOneTwo(error)
                            handleErrorThree(error)
                        }
                    }
                }
                .addTo(compositeDisposable)
    }

    private fun deleteAndSavePosts(postsDelete: MutableList<PostSearch>, postSave: PostSearch) {
        Completable.fromAction { database.postSearchDao().delete(postsDelete) }
                .performOnBack(scheduler)
                .doOnComplete {
                    saveVotePost(postSave)
                }
                .subscribe()
    }

    private fun saveVotePost(postSearch: PostSearch) {
        Completable.fromAction { database.postSearchDao().insertPost(postSearch) }
                .performOnBack(scheduler)
                .subscribe()
    }

    private fun saveVotePosts(posts: MutableList<PostSearch>) {
        Completable.fromAction { database.postSearchDao().insertPosts(posts) }
                .performOnBack(scheduler)
                .subscribe()
    }

    private fun deleteVotePost(postSearch: PostSearch) {
        Completable.fromAction { database.postSearchDao().deletePost(postSearch.site!!, postSearch.keyword!!, postSearch.id) }
                .performOnBack(scheduler)
                .subscribe()
    }

    private fun deleteVotePosts(posts: MutableList<PostSearch>) {
        Completable.fromAction {
            posts.forEach { post ->
                database.postSearchDao().deletePost(post.site!!, post.keyword!!, post.id)
            }
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun handleErrorOneTwo(error: Throwable) {
        idsOutcomeOneTwo.failed(error)
    }

    override fun handleErrorThree(error: Throwable) {
        idsOutcomeThree.failed(error)
    }

}