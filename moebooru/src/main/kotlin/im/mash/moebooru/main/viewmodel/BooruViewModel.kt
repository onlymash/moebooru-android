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
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.model.BooruDataContract
import im.mash.moebooru.util.logi
import io.reactivex.disposables.CompositeDisposable

class BooruViewModel(private val repo: BooruDataContract.Repository) : ViewModel() {

    companion object {
        private const val TAG = "BooruViewModel"
    }

    private val compositeDisposable = CompositeDisposable()

    val booruOutcome: LiveData<Outcome<MutableList<Booru>>> by lazy {
        repo.booruFetchOutcome.toLiveData(compositeDisposable)
    }

    fun loadBoorus() {
        repo.loadBoorus()
    }

    fun addBooru(booru: Booru) {
        repo.addBooru(booru)
    }

    fun addBoorus(boorus: MutableList<Booru>) {
        repo.addBoorus(boorus)
    }

    fun deleteBooru(booru: Booru) {
        repo.deleteBooru(booru)
    }

}