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

package im.mash.moebooru.main.model

import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

interface PostDataContract {
    interface Repository {
        val postFetchOutcome: PublishSubject<Outcome<MutableList<Post>>>
        val isEndOutCome: PublishSubject<Outcome<Boolean>>
        fun isNotMore(): Boolean
        fun fetchPosts(httpUrl: HttpUrl)
        fun refreshPosts(httpUrl: HttpUrl)
        fun loadMorePosts(httpUrl: HttpUrl)
        fun addPosts(posts: MutableList<Post>)
        fun deletePosts(site: String, limit: Int)
        fun handleError(error: Throwable)
    }
    interface Local {
        fun getPosts(site: String): Flowable<MutableList<Post>>
        fun addPosts(posts: MutableList<Post>)
        fun deletePosts(site: String, limit: Int)
    }
    interface Remote {
        fun getPosts(httpUrl: HttpUrl): Single<MutableList<Post>>
    }
}