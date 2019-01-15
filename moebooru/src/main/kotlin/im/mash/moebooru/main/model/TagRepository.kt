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

import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.local.entity.Tag
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class TagRepository(private val database: MoeDatabase,
                    private val scheduler: Scheduler,
                    private val compositeDisposable: CompositeDisposable) : TagDataContract.Repository {

    companion object {
        private const val TAG = "TagRepository"
    }

    override val tagFetchOutcome: PublishSubject<Outcome<MutableList<Tag>>>
            = PublishSubject.create<Outcome<MutableList<Tag>>>()

    override fun getTags(site: String) {
        tagFetchOutcome.loading(true)
        database.tagDao().getTags(site)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ tags ->
                    tagFetchOutcome.success(tags)
                }, { error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun saveTag(tag: Tag) {
        Completable.fromAction{
            database.tagDao().insertTag(tag)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deleteTag(tag: Tag) {
        Completable.fromAction{
            database.tagDao().deleteTag(tag)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun handleError(error: Throwable) {
        tagFetchOutcome.failed(error)
    }
}