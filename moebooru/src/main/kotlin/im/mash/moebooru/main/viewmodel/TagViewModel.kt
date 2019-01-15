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
import im.mash.moebooru.common.data.local.entity.Tag
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.model.TagDataContract
import io.reactivex.disposables.CompositeDisposable

class TagViewModel(private val repo: TagDataContract.Repository,
                   private val compositeDisposable: CompositeDisposable) : ViewModel() {

    val tagOutcome: LiveData<Outcome<MutableList<Tag>>> by lazy {
        repo.tagFetchOutcome.toLiveData(compositeDisposable)
    }

    fun loadTags(site: String) {
        repo.getTags(site)
    }

    fun deleteTag(tag: Tag) {
        repo.deleteTag(tag)
    }

    fun saveTag(tag: Tag) {
        repo.saveTag(tag)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
