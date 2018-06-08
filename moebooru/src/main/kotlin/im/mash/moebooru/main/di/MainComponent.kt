package im.mash.moebooru.main.di

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.data.remote.PostService
import im.mash.moebooru.core.constants.Constants
import im.mash.moebooru.core.module.CoreComponent
import im.mash.moebooru.core.network.Scheduler
import im.mash.moebooru.main.MainActivity
import im.mash.moebooru.main.model.*
import im.mash.moebooru.main.viewmodel.PostViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit

@MainScope
@Component(dependencies = [CoreComponent::class], modules = [MainModule::class])
interface MainComponent {

    //Expose to dependent components
    fun database(): MoeDatabase
    fun postService(): PostService
    fun scheduler(): Scheduler
    fun inject(MainActivity: MainActivity)
}

@Module
class MainModule {

    /*ViewModel*/
    @Provides
    @MainScope
    fun postViewModelFactory(repository: PostDataContract.Repository,
                             compositeDisposable: CompositeDisposable): PostViewModelFactory
            = PostViewModelFactory(repository,compositeDisposable)

    /*Repository*/
    @Provides
    @MainScope
    fun postRepo(local: PostDataContract.Local, remote: PostDataContract.Remote, scheduler: Scheduler,
                 compositeDisposable: CompositeDisposable): PostDataContract.Repository
            = PostRepository(local, remote, scheduler, compositeDisposable)

    @Provides
    @MainScope
    fun remoteData(postService: PostService): PostDataContract.Remote = RemoteData(postService)

    @Provides
    @MainScope
    fun localData(database: MoeDatabase, scheduler: Scheduler): PostDataContract.Local = PostLocalData(database, scheduler)

    @Provides
    @MainScope
    fun compositeDisposable(): CompositeDisposable = CompositeDisposable()

    /*Parent providers to dependents*/
    @Provides
    @MainScope
    fun database(context: Context): MoeDatabase
            = Room.databaseBuilder(context, MoeDatabase::class.java, Constants.DB_NAME).build()

    @Provides
    @MainScope
    fun postService(retrofit: Retrofit): PostService = retrofit.create(PostService::class.java)
}