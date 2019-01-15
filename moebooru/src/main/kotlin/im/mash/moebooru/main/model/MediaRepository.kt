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

import im.mash.moebooru.common.data.media.MediaStoreDataSource
import im.mash.moebooru.common.data.media.entity.MediaStoreData
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class MediaRepository(private val source: MediaStoreDataSource,
                      private val scheduler: Scheduler) : MediaDataContract.Repository {

    private val compositeDisposable = CompositeDisposable()

    override val mediaOutcome: PublishSubject<Outcome<MutableList<MediaStoreData>>>
            = PublishSubject.create<Outcome<MutableList<MediaStoreData>>>()

    override fun getMedia(path: String) {
        mediaOutcome.loading(true)
        source.loadMediaData(path)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ data ->
                    mediaOutcome.success(data)
                }, { error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun handleError(error: Throwable) {
        mediaOutcome.failed(error)
    }
}