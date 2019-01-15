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

package im.mash.moebooru.detail.di

import dagger.Component
import dagger.Module
import dagger.Provides
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.remote.CommentService
import im.mash.moebooru.common.data.remote.VoteService
import im.mash.moebooru.common.di.CoreComponent
import im.mash.moebooru.common.model.*
import im.mash.moebooru.common.viewmodel.CommentViewModelFactory
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.detail.model.DetailDataContract
import im.mash.moebooru.detail.model.DetailLocalData
import im.mash.moebooru.detail.model.DetailRepository
import im.mash.moebooru.detail.viewmodel.DetailViewModelFactory
import im.mash.moebooru.detail.viewmodel.PositionViewModelFactory
import im.mash.moebooru.main.model.TagDataContract
import im.mash.moebooru.main.model.TagRepository
import im.mash.moebooru.common.viewmodel.DownloadViewModelFactory
import im.mash.moebooru.common.viewmodel.VoteViewModelFactory
import im.mash.moebooru.main.viewmodel.TagViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit

@DetailScope
@Component(dependencies = [CoreComponent::class], modules = [DetailModule::class])
interface DetailComponent {
    fun inject(detailActivity: DetailActivity)
}

@Module
class DetailModule {

    @Provides
    @DetailScope
    fun compositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @DetailScope
    fun detailViewModelFactory(repo: DetailDataContract.Repository, compositeDisposable: CompositeDisposable): DetailViewModelFactory
            = DetailViewModelFactory(repo, compositeDisposable)

    @Provides
    @DetailScope
    fun detailRepo(local: DetailDataContract.Local, scheduler: Scheduler, compositeDisposable: CompositeDisposable): DetailDataContract.Repository
            = DetailRepository(local, scheduler,compositeDisposable)

    @Provides
    @DetailScope
    fun localData(database: MoeDatabase, scheduler: Scheduler): DetailDataContract.Local
            = DetailLocalData(database, scheduler)

    @Provides
    @DetailScope
    fun positionViewModelFactory(): PositionViewModelFactory = PositionViewModelFactory()

    @Provides
    @DetailScope
    fun downloadViewModelFactory(repo: DownloadDataContract.Repository,
                                 compositeDisposable: CompositeDisposable): DownloadViewModelFactory
            = DownloadViewModelFactory(repo, compositeDisposable)

    @Provides
    @DetailScope
    fun downloadRepo(database: MoeDatabase, scheduler: Scheduler,
                     compositeDisposable: CompositeDisposable): DownloadDataContract.Repository
            = DownloadRepository(database, scheduler, compositeDisposable)

    @Provides
    @DetailScope
    fun tagViewModelFactory(repository: TagDataContract.Repository,
                            compositeDisposable: CompositeDisposable): TagViewModelFactory
            = TagViewModelFactory(repository,compositeDisposable)

    @Provides
    @DetailScope
    fun tagRepo(database: MoeDatabase, scheduler: Scheduler, compositeDisposable: CompositeDisposable) : TagDataContract.Repository
            = TagRepository(database, scheduler, compositeDisposable)

    @Provides
    @DetailScope
    fun voteService(retrofit: Retrofit): VoteService = retrofit.create(VoteService::class.java)

    @Provides
    @DetailScope
    fun voteRepo(voteService: VoteService, database: MoeDatabase, scheduler: Scheduler,
                 compositeDisposable: CompositeDisposable): VoteDataContract.Repository
            = VoteRepository(voteService, database, scheduler, compositeDisposable)

    @Provides
    @DetailScope
    fun voteViewModelFactory(voteRepo: VoteDataContract.Repository,
                             compositeDisposable: CompositeDisposable): VoteViewModelFactory
            = VoteViewModelFactory(voteRepo, compositeDisposable)

    @Provides
    @DetailScope
    fun localComment(database: MoeDatabase, scheduler: Scheduler): CommentDataContract.Local
            = CommentLocalData(database, scheduler)

    @Provides
    @DetailScope
    fun commentService(retrofit: Retrofit): CommentService = retrofit.create(CommentService::class.java)

    @Provides
    @DetailScope
    fun remoteComment(commentService: CommentService): CommentDataContract.Remote
            = CommentRemoteData(commentService)

    @Provides
    @DetailScope
    fun commentRepo(localComment: CommentDataContract.Local, remoteComment: CommentDataContract.Remote,
                    scheduler: Scheduler): CommentDataContract.Repository
            = CommentRepository(localComment, remoteComment, scheduler)

    @Provides
    @DetailScope
    fun commentViewModelFactory(repo: CommentDataContract.Repository): CommentViewModelFactory
            = CommentViewModelFactory(repo)
}