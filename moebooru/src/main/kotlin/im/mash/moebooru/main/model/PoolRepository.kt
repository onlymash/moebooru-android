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
import im.mash.moebooru.common.data.local.entity.Pool
import im.mash.moebooru.common.data.remote.PoolService
import im.mash.moebooru.core.extensions.*
import im.mash.moebooru.core.scheduler.Outcome
import im.mash.moebooru.core.scheduler.Scheduler
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import okhttp3.HttpUrl

class PoolRepository(private val poolService: PoolService,
                     private val database: MoeDatabase,
                     private val scheduler: Scheduler) : PoolDataContract.Repository {

    private val compositeDisposable = CompositeDisposable()

    private var notMore = false
    override fun isNotMore(): Boolean = notMore

    override val isEndOutCome: PublishSubject<Outcome<Boolean>>
            = PublishSubject.create<Outcome<Boolean>>()

    override val poolFetchOutcome: PublishSubject<Outcome<MutableList<Pool>>>
            = PublishSubject.create<Outcome<MutableList<Pool>>>()

    override fun getPools(host: String) {
        poolFetchOutcome.loading(true)
        database.poolDao()
                .getPools(host)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ pools ->
                    poolFetchOutcome.success(pools)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun refreshPools(url: HttpUrl) {
        notMore = false
        poolService.getPools(url)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ pools ->
                    val host = url.host()
                    pools.forEach { pool ->
                        pool.host = host
                    }
                    deleteAndSavePools(pools, url.host(), 20)
                    isEndOutCome.success(true)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    override fun loadMorePools(url: HttpUrl) {
        poolService.getPools(url)
                .performOnBackOutOnMain(scheduler)
                .subscribe({ pools ->
                    val host = url.host()
                    pools.forEach { pool ->
                        pool.host = host
                    }
                    if (pools.size < 20) {
                        notMore = true
                    }
                    savePools(pools)
                    isEndOutCome.success(true)
                }, { error -> handleError(error)})
                .addTo(compositeDisposable)
    }

    private fun deleteAndSavePools(pools: MutableList<Pool>, host: String, limit: Int) {
        Completable.fromAction {
            database.poolDao().deletePools(host, limit)
        }
                .performOnBack(scheduler)
                .doOnComplete {
                    savePools(pools)
                }
                .subscribe()
    }

    override fun savePools(pools: MutableList<Pool>) {
        Completable.fromAction {
            database.poolDao().insertBoorus(pools)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun deletePools(host: String, limit: Int) {
        Completable.fromAction {
            database.poolDao().deletePools(host, limit)
        }
                .performOnBack(scheduler)
                .subscribe()
    }

    override fun handleError(error: Throwable) {
        poolFetchOutcome.failed(error)
    }
}