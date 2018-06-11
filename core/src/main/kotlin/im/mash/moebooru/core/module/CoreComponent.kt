package im.mash.moebooru.core.module


import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import im.mash.moebooru.core.scheduler.Scheduler
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, StorageModule::class])
interface CoreComponent {

    fun context(): Context

    fun retrofit(): Retrofit

    fun httpClient(): OkHttpClient

    fun sharedPreferences(): SharedPreferences

    fun scheduler(): Scheduler
}