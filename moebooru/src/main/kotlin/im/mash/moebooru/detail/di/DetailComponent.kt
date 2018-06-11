package im.mash.moebooru.detail.di

import dagger.Component
import dagger.Module
import dagger.Provides
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.core.network.Scheduler
import im.mash.moebooru.detail.DetailActivity
import im.mash.moebooru.detail.model.DetailDataContract
import im.mash.moebooru.detail.model.DetailLocalData
import im.mash.moebooru.detail.model.DetailRepository
import im.mash.moebooru.detail.viewmodel.DetailViewModelFactory
import im.mash.moebooru.detail.viewmodel.PositionViewModelFactory
import im.mash.moebooru.main.di.MainComponent
import io.reactivex.disposables.CompositeDisposable

@DetailScope
@Component(dependencies = [MainComponent::class], modules = [DetailModule::class])
interface DetailComponent {
    fun inject(detailActivity: DetailActivity)
}

@Module
class DetailModule {

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
    fun compositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @DetailScope
    fun positionViewModelFactory(): PositionViewModelFactory = PositionViewModelFactory()
}