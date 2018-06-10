package im.mash.moebooru.common.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.support.annotation.Keep
import im.mash.moebooru.common.data.local.dao.BooruDao
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.dao.PostDao
import im.mash.moebooru.common.data.local.dao.PostSearchDao
import im.mash.moebooru.common.data.local.dao.TagDao
import im.mash.moebooru.common.data.local.entity.Booru
import im.mash.moebooru.common.data.local.entity.PostSearch
import im.mash.moebooru.common.data.local.entity.Tag

@Keep
@Database(entities = [(Booru::class), (Post::class), (PostSearch::class), (Tag::class)],
        version = 1, exportSchema = false)
abstract class MoeDatabase : RoomDatabase() {

    abstract fun booruDao(): BooruDao

    abstract fun postDao(): PostDao

    abstract fun postSearchDao(): PostSearchDao

    abstract fun tagDao(): TagDao

}