package im.mash.moebooru.main.di

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.media.MediaStoreDataSource
import im.mash.moebooru.common.data.remote.*
import im.mash.moebooru.common.di.CoreComponent
import im.mash.moebooru.common.model.*
import im.mash.moebooru.common.viewmodel.DownloadViewModelFactory
import im.mash.moebooru.common.viewmodel.UserViewModelFactory
import im.mash.moebooru.common.viewmodel.VoteViewModelFactory
import im.mash.moebooru.core.scheduler.Scheduler
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.model.*
import im.mash.moebooru.main.viewmodel.*
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit

@MainScope
@Component(dependencies = [CoreComponent::class], modules = [MainModule::class])
interface MainComponent {
    fun inject(mainActivity: MainActivity)
}

@Module
class MainModule {

    /*PostViewModel*/
    @Provides
    @MainScope
    fun postViewModelFactory(repository: PostDataContract.Repository,
                             compositeDisposable: CompositeDisposable): PostViewModelFactory
            = PostViewModelFactory(repository,compositeDisposable)

    /*PostRepository*/
    @Provides
    @MainScope
    fun postRepo(local: PostDataContract.Local, remote: PostDataContract.Remote, scheduler: Scheduler,
                 compositeDisposable: CompositeDisposable): PostDataContract.Repository
            = PostRepository(local, remote, scheduler, compositeDisposable)

    @Provides
    @MainScope
    fun remotePostData(postService: PostService): PostDataContract.Remote = PostRemoteData(postService)

    @Provides
    @MainScope
    fun localPostData(database: MoeDatabase, scheduler: Scheduler): PostDataContract.Local = PostLocalData(database, scheduler)

    @Provides
    @MainScope
    fun compositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @MainScope
    fun postService(retrofit: Retrofit): PostService = retrofit.create(PostService::class.java)

    @Provides
    @MainScope
    fun postSearchService(retrofit: Retrofit): PostSearchService = retrofit.create(PostSearchService::class.java)

    /*BooruViewModel*/
    @Provides
    @MainScope
    fun booruViewModelFactory(repository: BooruDataContract.Repository,
                             compositeDisposable: CompositeDisposable): BooruViewModelFactory
            = BooruViewModelFactory(repository,compositeDisposable)

    @Provides
    @MainScope
    fun booruRepo(local: BooruDataContract.Local, scheduler: Scheduler, compositeDisposable: CompositeDisposable) : BooruDataContract.Repository
            = BooruRepository(local, scheduler, compositeDisposable)

    @Provides
    @MainScope
    fun localBooruData(database: MoeDatabase, scheduler: Scheduler): BooruDataContract.Local = BooruLocalData(database, scheduler)

    /*TagViewModel*/
    @Provides
    @MainScope
    fun tagViewModelFactory(repository: TagDataContract.Repository,
                             compositeDisposable: CompositeDisposable): TagViewModelFactory
            = TagViewModelFactory(repository,compositeDisposable)

    @Provides
    @MainScope
    fun tagRepo(database: MoeDatabase, scheduler: Scheduler, compositeDisposable: CompositeDisposable) : TagDataContract.Repository
            = TagRepository(database, scheduler, compositeDisposable)


    @Provides
    @MainScope
    fun mediaStoreDataSource(context: Context): MediaStoreDataSource = MediaStoreDataSource(context)

    /*MediaViewModel*/
    @Provides
    @MainScope
    fun mediaViewModelFactory(repository: MediaDataContract.Repository,
                             compositeDisposable: CompositeDisposable): MediaViewModelFactory
            = MediaViewModelFactory(repository,compositeDisposable)

    /*MediaRepository*/
    @Provides
    @MainScope
    fun mediaRepo(source: MediaStoreDataSource, scheduler: Scheduler, compositeDisposable: CompositeDisposable): MediaDataContract.Repository
            = MediaRepository(source, scheduler, compositeDisposable)

    @Provides
    @MainScope
    fun downloadViewModelFactory(downloadRepo: DownloadDataContract.Repository,
                                 compositeDisposable: CompositeDisposable): DownloadViewModelFactory
            = DownloadViewModelFactory(downloadRepo, compositeDisposable)

    @Provides
    @MainScope
    fun downloadRepo(database: MoeDatabase, scheduler: Scheduler,
                     compositeDisposable: CompositeDisposable): DownloadDataContract.Repository
            = DownloadRepository(database, scheduler, compositeDisposable)

    @Provides
    @MainScope
    fun userService(retrofit: Retrofit): UserService = retrofit.create(UserService::class.java)

    @Provides
    @MainScope
    fun userRepo(database: MoeDatabase, userService: UserService,
                 scheduler: Scheduler, compositeDisposable: CompositeDisposable): UserDataContract.Repository
            = UserRepository(database, userService, scheduler, compositeDisposable)

    @Provides
    @MainScope
    fun userViewModelFactory(userRepo: UserDataContract.Repository, compositeDisposable: CompositeDisposable)
            = UserViewModelFactory(userRepo, compositeDisposable)

    @Provides
    @MainScope
    fun voteService(retrofit: Retrofit): VoteService = retrofit.create(VoteService::class.java)

    @Provides
    @MainScope
    fun voteRepo(voteService: VoteService, database: MoeDatabase, scheduler: Scheduler,
                 compositeDisposable: CompositeDisposable): VoteDataContract.Repository
            = VoteRepository(voteService, database, scheduler, compositeDisposable)

    @Provides
    @MainScope
    fun voteViewModelFactory(voteRepo: VoteDataContract.Repository,
                             compositeDisposable: CompositeDisposable): VoteViewModelFactory
            = VoteViewModelFactory(voteRepo, compositeDisposable)

    @Provides
    @MainScope
    fun poolService(retrofit: Retrofit): PoolService = retrofit.create(PoolService::class.java)
}