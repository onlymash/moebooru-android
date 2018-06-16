package im.mash.moebooru.common.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.support.annotation.Keep
import im.mash.moebooru.common.data.local.dao.*
import im.mash.moebooru.common.data.local.entity.*

@Keep
@Database(entities = [(Booru::class), (Post::class), (PostSearch::class),
    (Tag::class), (PostDownload::class)], version = 2, exportSchema = false)
abstract class MoeDatabase : RoomDatabase() {

    abstract fun booruDao(): BooruDao

    abstract fun postDao(): PostDao

    abstract fun postSearchDao(): PostSearchDao

    abstract fun tagDao(): TagDao

    abstract fun postDownloadDao(): PostDownloadDao

}