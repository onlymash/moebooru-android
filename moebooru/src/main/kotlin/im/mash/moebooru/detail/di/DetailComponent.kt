package im.mash.moebooru.detail.di

import dagger.Component
import dagger.Module
import dagger.Provides
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.di.CoreComponent
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.detail.model.DetailDataContract
import im.mash.moebooru.detail.model.DetailLocalData
import im.mash.moebooru.detail.model.DetailRepository
import im.mash.moebooru.detail.viewmodel.DetailViewModelFactory
import im.mash.moebooru.detail.viewmodel.PositionViewModelFactory
import im.mash.moebooru.common.model.DownloadDataContract
import im.mash.moebooru.common.model.DownloadRepository
import im.mash.moebooru.main.model.TagDataContract
import im.mash.moebooru.main.model.TagRepository
import im.mash.moebooru.common.viewmodel.DownloadViewModelFactory
import im.mash.moebooru.main.viewmodel.TagViewModelFactory
import io.reactivex.disposables.CompositeDisposable

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
}