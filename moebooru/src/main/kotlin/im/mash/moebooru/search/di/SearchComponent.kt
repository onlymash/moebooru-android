package im.mash.moebooru.search.di

import dagger.Component
import dagger.Module
import dagger.Provides
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.remote.PostSearchService
import im.mash.moebooru.common.data.remote.UserService
import im.mash.moebooru.common.data.remote.VoteService
import im.mash.moebooru.common.di.CoreComponent
import im.mash.moebooru.common.model.UserDataContract
import im.mash.moebooru.common.model.UserRepository
import im.mash.moebooru.common.model.VoteDataContract
import im.mash.moebooru.common.model.VoteRepository
import im.mash.moebooru.common.viewmodel.UserViewModelFactory
import im.mash.moebooru.common.viewmodel.VoteViewModelFactory
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.search.SearchActivity
import im.mash.moebooru.search.model.PostSearchDataContract
import im.mash.moebooru.search.model.PostSearchLocalData
import im.mash.moebooru.search.model.PostSearchRemoteData
import im.mash.moebooru.search.model.PostSearchRepository
import im.mash.moebooru.search.viewmodel.PostSearchViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit

@SearchScope
@Component(dependencies = [CoreComponent::class], modules = [SearchModule::class])
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
    fun remoteSearchData(postSearchService: PostSearchService): PostSearchDataContract.Remote
            = PostSearchRemoteData(postSearchService)

    @Provides
    @SearchScope
    fun postSearchRepo(local: PostSearchDataContract.Local, remote: PostSearchDataContract.Remote, scheduler: Scheduler,
                       compositeDisposable: CompositeDisposable): PostSearchDataContract.Repository
            = PostSearchRepository(local, remote, scheduler, compositeDisposable)

    @Provides
    @SearchScope
    fun userService(retrofit: Retrofit): UserService = retrofit.create(UserService::class.java)

    @Provides
    @SearchScope
    fun userRepo(database: MoeDatabase, userService: UserService,
                 scheduler: Scheduler, compositeDisposable: CompositeDisposable): UserDataContract.Repository
            = UserRepository(database, userService, scheduler, compositeDisposable)

    @Provides
    @SearchScope
    fun userViewModelFactory(repository: UserDataContract.Repository, compositeDisposable: CompositeDisposable)
            = UserViewModelFactory(repository, compositeDisposable)

    @Provides
    @SearchScope
    fun voteService(retrofit: Retrofit): VoteService = retrofit.create(VoteService::class.java)

    @Provides
    @SearchScope
    fun voteRepo(voteService: VoteService, database: MoeDatabase, scheduler: Scheduler,
                 compositeDisposable: CompositeDisposable): VoteDataContract.Repository
            = VoteRepository(voteService, database, scheduler, compositeDisposable)

    @Provides
    @SearchScope
    fun voteViewModelFactory(voteRepo: VoteDataContract.Repository,
                             compositeDisposable: CompositeDisposable): VoteViewModelFactory
            = VoteViewModelFactory(voteRepo, compositeDisposable)
}