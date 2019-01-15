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
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.util.logi
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class BooruRepository(private val database: MoeDatabase,
                      private val scheduler: Scheduler) : BooruDataContract.Repository {

    companion object {
        private const val TAG = "BooruRepository"
    }

    private val compositeDisposable = CompositeDisposable()

    override val booruFetchOutcome: PublishSubject<Outcome<MutableList<Booru>>>
            = PublishSubject.create<Outcome<MutableList<Booru>>>()

    override fun loadBoorus() {
        booruFetchOutcome.loading(true)
        database.booruDao()
                .getBoorus()
                .performOnBackOutOnMain(scheduler)
                .subscribe({ boorus ->
                    logi(TAG, "loadBoorus success")
                    booruFetchOutcome.success(boorus)
                }, { error -> handleError(error) })
                .addTo(compositeDisposable)
    }

    override fun deleteBooru(booru: Booru) {
        Completable.fromAction {
            database.booruDao().delete(booru)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun addBooru(booru: Booru) {
        Completable.fromAction {
            database.booruDao().insertBooru(booru)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun addBoorus(boorus: MutableList<Booru>) {
        Completable.fromAction {
            database.booruDao().insertBoorus(boorus)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun handleError(error: Throwable) {
        booruFetchOutcome.failed(error)
    }
}