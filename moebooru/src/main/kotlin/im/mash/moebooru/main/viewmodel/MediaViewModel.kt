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
import im.mash.moebooru.common.data.media.entity.MediaStoreData
import im.mash.moebooru.core.extensions.toLiveData
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.main.model.MediaDataContract
import io.reactivex.disposables.CompositeDisposable

class MediaViewModel(private val repo: MediaDataContract.Repository) : ViewModel() {

    companion object {
        private const val TAG = "MediaViewModel"
    }

    private val compositeDisposable = CompositeDisposable()

    val mediaOutcome: LiveData<Outcome<MutableList<MediaStoreData>>> by lazy {
        repo.mediaOutcome.toLiveData(compositeDisposable)
    }

    fun loadMedia(path: String) {
        repo.getMedia(path)
    }

    override fun onCleared() {
        super.onCleared()
        //clear the disposables when the viewmodel is cleared
        compositeDisposable.clear()
    }
}