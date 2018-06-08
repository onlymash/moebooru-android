package im.mash.moebooru.core.module

import android.content.Context
import dagger.Module
import dagger.Provides
import im.mash.moebooru.core.network.AppScheduler
import im.mash.moebooru.core.network.Scheduler
import javax.inject.Singleton

@Module
class AppModule(val context: Context) {

    @Provides
    @Singleton
    fun providesContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun scheduler(): Scheduler {
        return AppScheduler()
    }
}