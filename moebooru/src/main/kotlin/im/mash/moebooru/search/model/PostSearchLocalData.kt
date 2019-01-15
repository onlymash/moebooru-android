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


import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.extensions.performOnBack
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
import io.reactivex.Flowable

class PostSearchLocalData(private val database: MoeDatabase,
                    private val scheduler: Scheduler)
    : PostSearchDataContract.Local {

    companion object {
        private const val TAG = "PostSearchLocalData"
    }

    override fun getPosts(site: String, tags: String): Flowable<MutableList<PostSearch>> {
        return database.postSearchDao().getPosts(site, tags)
    }

    override fun addPosts(posts: MutableList<PostSearch>) {
        Completable.fromAction{
            database.postSearchDao().insertPosts(posts)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deletePosts(site: String, tags: String, limit: Int) {
        database.postSearchDao().deletePosts(site, tags, limit)
    }
}