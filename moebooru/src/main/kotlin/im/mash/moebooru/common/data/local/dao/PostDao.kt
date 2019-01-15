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

package im.mash.moebooru.common.data.local.dao

import androidx.room.*
import im.mash.moebooru.common.data.local.entity.Post
import io.reactivex.Flowable

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAll(): Flowable<MutableList<Post>>

    @Query("SELECT * FROM posts WHERE site = :site AND id = :id")
    fun getPost(site: String, id: Int): Flowable<Post>

    @Query("SELECT * FROM posts WHERE site = :site ORDER BY id DESC")
    fun getPosts(site: String): Flowable<MutableList<Post>>

    @Query("DELETE FROM posts WHERE site = :site")
    fun deletePosts(site: String)

    @Query("DELETE FROM posts WHERE site = :site AND id NOT IN (SELECT id FROM posts WHERE site = :site ORDER BY id DESC LIMIT :limit)")
    fun deletePosts(site: String, limit: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPosts(posts: MutableList<Post>)

    @Delete
    fun delete(post: Post)
}