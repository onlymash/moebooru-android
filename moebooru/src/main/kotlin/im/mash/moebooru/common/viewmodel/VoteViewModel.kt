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

package im.mash.moebooru.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import im.mash.moebooru.common.model.VoteDataContract
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import io.reactivex.disposables.CompositeDisposable

class VoteViewModel(private val repo: VoteDataContract.Repository,
                    private val compositeDisposable: CompositeDisposable) : ViewModel(){

    val idsOutcomeOneTwo: LiveData<Outcome<MutableList<Int>>> by lazy {
        repo.idsOutcomeOneTwo.toLiveData(compositeDisposable)
    }

    val idsOutcomeThree: LiveData<Outcome<MutableList<Int>>> by lazy {
        repo.idsOutcomeThree.toLiveData(compositeDisposable)
    }

    fun getVoteIdsOneTwo(site: String, username: String) {
        repo.getVoteIdsOneTwo(site, username)
    }

    fun getVoteIdsThree(site: String, username: String) {
        repo.getVoteIdsThree(site, username)
    }

    fun votePost(url: String, id: Int, score: Int, username: String, passwordHash: String) {
        repo.votePost(url, id, score, username, passwordHash)
    }
}