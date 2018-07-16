package im.mash.moebooru.common.module

import androidx.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import im.mash.moebooru.common.data.local.MoeDatabase
import im.mash.moebooru.core.constants.Constants
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun database(context: Context): MoeDatabase
            = Room.databaseBuilder(context, MoeDatabase::class.java, Constants.DB_NAME).build()
}