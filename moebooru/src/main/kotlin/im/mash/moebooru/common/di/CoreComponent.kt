package im.mash.moebooru.common.di


import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.common.module.DatabaseModule
import im.mash.moebooru.core.module.AppModule
import im.mash.moebooru.core.module.NetworkModule
import im.mash.moebooru.core.module.StorageModule
import im.mash.moebooru.core.scheduler.Scheduler
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, StorageModule::class, DatabaseModule::class])
interface CoreComponent {

    fun context(): Context

    fun retrofit(): Retrofit

    fun httpClient(): OkHttpClient

    fun sharedPreferences(): SharedPreferences

    fun scheduler(): Scheduler

    fun database(): MoeDatabase
}