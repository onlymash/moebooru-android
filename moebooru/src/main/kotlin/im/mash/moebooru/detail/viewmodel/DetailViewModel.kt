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

package im.mash.moebooru.detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.MoeDH
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.detail.model.DetailDataContract
import io.reactivex.disposables.CompositeDisposable

class DetailViewModel(private val repo: DetailDataContract.Repository,
                      private val compositeDisposable: CompositeDisposable) : ViewModel() {

    val postOutcome: LiveData<Outcome<MutableList<Post>>> by lazy {
        repo.postFetchOutcome.toLiveData(compositeDisposable)
    }

    val postSearchOutcome: LiveData<Outcome<MutableList<PostSearch>>> by lazy {
        repo.postSearchFetchOutcome.toLiveData(compositeDisposable)
    }

    fun loadPosts(site: String) {
        repo.fetchPosts(site)
    }

    fun loadPosts(site: String, tags: String) {
        repo.fetchPosts(site, tags)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        MoeDH.destroyDetailComponent()
    }
}