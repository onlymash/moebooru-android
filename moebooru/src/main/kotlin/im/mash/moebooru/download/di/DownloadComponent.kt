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

package im.mash.moebooru.download.di

import dagger.Component
import dagger.Module
import dagger.Provides
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.di.CoreComponent
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.download.DownloadService
import im.mash.moebooru.common.model.DownloadDataContract
import im.mash.moebooru.common.model.DownloadRepository
import im.mash.moebooru.common.viewmodel.DownloadViewModelFactory
import io.reactivex.disposables.CompositeDisposable

@DownloadScope
@Component(dependencies = [CoreComponent::class], modules = [DownloadModule::class])
interface DownloadComponent {
    fun inject(downloadService: DownloadService)
}

@Module
class DownloadModule {

    @Provides
    @DownloadScope
    fun compositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @DownloadScope
    fun downloadViewModelFactory(repo: DownloadDataContract.Repository,
                                 compositeDisposable: CompositeDisposable): DownloadViewModelFactory
            = DownloadViewModelFactory(repo, compositeDisposable)

    @Provides
    @DownloadScope
    fun downloadRepo(database: MoeDatabase, scheduler: Scheduler,
                     compositeDisposable: CompositeDisposable): DownloadDataContract.Repository
            = DownloadRepository(database, scheduler, compositeDisposable)
}