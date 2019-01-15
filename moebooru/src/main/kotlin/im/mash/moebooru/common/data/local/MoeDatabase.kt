/*
 * Copyright (C) 2019 by onlymash <im@fiepi.me>, All rights reserved
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

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