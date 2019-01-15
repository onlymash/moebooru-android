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

package im.mash.moebooru.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.model.PostDataContract
import im.mash.moebooru.util.logi
import io.reactivex.disposables.CompositeDisposable
import okhttp3.HttpUrl

class PostViewModel(private val repo: PostDataContract.Repository,
                    private val compositeDisposable: CompositeDisposable) : ViewModel() {

    companion object {
        private const val TAG = "PostViewModel"
    }

    val postsOutcome: LiveData<Outcome<MutableList<Post>>> by lazy {
        //Convert publish subject to livedata
        repo.postFetchOutcome.toLiveData(compositeDisposable)
    }

    val isEndOutcome: LiveData<Outcome<Boolean>> by lazy {
        repo.isEndOutCome.toLiveData(compositeDisposable)
    }

    fun isNotMore(): Boolean {
        return repo.isNotMore()
    }

    fun loadPosts(httpUrl: HttpUrl) {
        repo.fetchPosts(httpUrl)
    }

    fun refreshPosts(httpUrl: HttpUrl) {
        repo.refreshPosts(httpUrl)
    }

    fun loadMorePosts(httpUrl: HttpUrl) {
        repo.loadMorePosts(httpUrl)
    }

    fun deletePosts(site: String, limit: Int) {
        repo.deletePosts(site, limit)
    }

    fun clear() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        //clear the disposables when the viewmodel is cleared
        compositeDisposable.clear()
    }
}