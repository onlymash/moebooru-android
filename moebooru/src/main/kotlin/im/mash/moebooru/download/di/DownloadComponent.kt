package im.mash.moebooru.download.di

import dagger.Component
import dagger.Module
import dagger.Provides
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.download.DownloadService
import im.mash.moebooru.main.di.MainComponent
import im.mash.moebooru.main.model.DownloadDataContract
import im.mash.moebooru.main.model.DownloadRepository
import im.mash.moebooru.main.viewmodel.DownloadViewModelFactory
import io.reactivex.disposables.CompositeDisposable

@DownloadScope
@Component(dependencies = [MainComponent::class], modules = [DownloadModule::class])
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
            = DownloadViewModelFactory(repo,compositeDisposable)

    @Provides
    @DownloadScope
    fun downloadRepo(database: MoeDatabase, scheduler: Scheduler,
                     compositeDisposable: CompositeDisposable): DownloadDataContract.Repository
            = DownloadRepository(database, scheduler, compositeDisposable)
}