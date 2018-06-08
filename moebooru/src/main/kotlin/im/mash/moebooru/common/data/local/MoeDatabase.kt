package im.mash.moebooru.common.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import im.mash.moebooru.common.data.local.entity.Post
import im.mash.moebooru.common.data.local.dao.PostDao
import im.mash.moebooru.common.data.local.dao.PostSearchDao
import im.mash.moebooru.common.data.local.entity.PostSearch

@Database(entities = [(Post::class), (PostSearch::class)], version = 1, exportSchema = false)
abstract class MoeDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

    abstract fun postSearchDao(): PostSearchDao

}