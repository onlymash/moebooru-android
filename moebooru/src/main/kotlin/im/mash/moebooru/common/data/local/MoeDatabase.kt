package im.mash.moebooru.common.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.annotation.Keep
import im.mash.moebooru.common.data.local.dao.*
import im.mash.moebooru.common.data.local.entity.*

@Keep
@Database(entities = [(Booru::class), (Post::class), (PostSearch::class), (Tag::class),
    (PostDownload::class), (User::class), (Pool::class), (Comment::class)], version = 3, exportSchema = false)
abstract class MoeDatabase : RoomDatabase() {

    abstract fun booruDao(): BooruDao

    abstract fun postDao(): PostDao

    abstract fun postSearchDao(): PostSearchDao

    abstract fun tagDao(): TagDao

    abstract fun postDownloadDao(): PostDownloadDao

    abstract fun userDao(): UserDao

    abstract fun poolDao(): PoolDao

    abstract fun commentDao(): CommentDao
}