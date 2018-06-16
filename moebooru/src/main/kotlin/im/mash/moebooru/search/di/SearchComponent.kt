package im.mash.moebooru.search.di

import dagger.Component
import dagger.Module
import dagger.Provides
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.remote.PostSearchService
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.main.di.MainComponent
import im.mash.moebooru.search.SearchActivity
import im.mash.moebooru.search.model.PostSearchDataContract
import im.mash.moebooru.search.model.PostSearchLocalData
import im.mash.moebooru.search.model.PostSearchRemoteData
import im.mash.moebooru.search.model.PostSearchRepository
import im.mash.moebooru.search.viewmodel.PostSearchViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit

@SearchScope
@Component(dependencies = [MainComponent::class], modules = [SearchModule::class])
interface SearchComponent {
    fun inject(searchActivity: SearchActivity)
}

@Module
class SearchModule {

    @Provides
    @SearchScope
    fun compositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @SearchScope
    fun postSearchViewModelFactory(repo: PostSearchDataContract.Repository, compositeDisposable: CompositeDisposable): PostSearchViewModelFactory
            = PostSearchViewModelFactory(repo, compositeDisposable)

    @Provides
    @SearchScope
    fun postSearchService(retrofit: Retrofit): PostSearchService
            = retrofit.create(PostSearchService::class.java)

    @Provides
    @SearchScope
    fun localSearchData(database: MoeDatabase, scheduler: Scheduler): PostSearchDataContract.Local
            = PostSearchLocalData(database, scheduler)

    @Provides
    @SearchScope
    fun remotePostData(postSearchService: PostSearchService): PostSearchDataContract.Remote
            = PostSearchRemoteData(postSearchService)

    @Provides
    @SearchScope
    fun postSearchRepo(local: PostSearchDataContract.Local, remote: PostSearchDataContract.Remote, scheduler: Scheduler,
                       compositeDisposable: CompositeDisposable): PostSearchDataContract.Repository
            = PostSearchRepository(local, remote, scheduler, compositeDisposable)
}